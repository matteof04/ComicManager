/*
 * Copyright (C) 2022 Matteo Franceschini <matteof5730@gmail.com>
 *
 * This file is part of ComicManager.
 * ComicManager is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ComicManager is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with ComicManager.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.matteof04.comicmanager.image

import com.github.matteof04.comicmanager.bookcreator.BookOptions
import com.github.matteof04.comicmanager.devices.base.DeviceInformation
import com.github.matteof04.comicmanager.image.util.*
import com.github.matteof04.comicmanager.util.Panel
import com.github.matteof04.comicmanager.util.PanelMetadata
import com.sksamuel.scrimage.*
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.filter.GaussianBlurFilter
import com.sksamuel.scrimage.nio.JpegWriter
import java.awt.Color
import java.awt.Rectangle
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

/**
 * Class for process and optimize images
 * @property temp [Path] the temporary directory where the class operates
 */
class ImageProcessor(private val deviceInformation: DeviceInformation) {
    private val temp = createTempDirectory("IMGPROC_")

    /**
     * Transform an image into a Panel with several optimization
     * @param path [Path] The path of image to transform
     * @param options [BookOptions] All the options for the panel preparation wrapped in an object
     * @return [ArrayList] Two panel if the passed image is a double-page image and the splitMode is set to SPLIT, one panel in other cases
     */
    fun preparePanel(
        path: Path,
        options: BookOptions
    ): ArrayList<Panel> {
        val image = ImmutableImage.loader().fromPath(path)
        if (image.width > image.height) {
            return when (options.splitMode) {
                DoublePagesHandlingMethod.ROTATE -> {
                    arrayListOf(
                        Panel(
                            prepareImage(image.rotateLeft(), options.resizeMode, options.backgroundColor, options.contrast).output(
                                JpegWriter.Default,
                                temp.resolve(path.toFile().nameWithoutExtension.padStart(4, '0') + ".jpg")
                            ), PanelMetadata.ROTATED
                        )
                    )
                }
                DoublePagesHandlingMethod.SPLIT -> {
                    val firstImage =
                        image.resizeTo(image.width / 2, image.height, Position.CenterLeft)
                    val secondImage =
                        image.resizeTo(image.width / 2, image.height, Position.CenterRight)
                    val preparedFirstImage = prepareImage(firstImage, options.resizeMode, BackgroundColors.NONE, options.contrast).output(
                        JpegWriter.Default,
                        temp.resolve(path.toFile().nameWithoutExtension.padStart(4, '0') + "-${options.pageProgressionDirection.getFirstSplitSuffix()}.jpg")
                    )
                    val preparedSecondImage = prepareImage(secondImage, options.resizeMode, BackgroundColors.NONE, options.contrast).output(
                        JpegWriter.Default,
                        temp.resolve(path.toFile().nameWithoutExtension.padStart(4, '0') + "-${options.pageProgressionDirection.getSecondSplitSuffix()}.jpg")
                    )
                    Panel.getDoublePanel(preparedFirstImage, preparedSecondImage, options.pageProgressionDirection)
                }
            }
        }
        return arrayListOf(
            Panel(
                prepareImage(image, options.resizeMode, options.backgroundColor, options.contrast).output(
                    JpegWriter.Default,
                    temp.resolve(path.toFile().nameWithoutExtension.padStart(4, '0') + ".jpg")
                )
            )
        )
    }

    /**
     * Prepare the image for the Panel
     * @param immutableImage [ImmutableImage] The image to prepare
     * @param resizeMode [ResizeModes] How the image must be resized
     * @param backgroundColor [BackgroundColors] The fill color for the screen
     * @param contrast [Double] The contrast value for the image (use 1.0 to leave the image as-is, set null to set auto contrast function)
     * @return [ImmutableImage] The prepared image
     */
    private fun prepareImage(
        immutableImage: ImmutableImage,
        resizeMode: ResizeModes,
        backgroundColor: BackgroundColors,
        contrast: Double? = 1.0
    ): ImmutableImage {
        var image = immutableImage
        image = cropMargin(image)
        image = resizeImage(image, resizeMode, backgroundColor)
        image = greyscaleAndDitherImage(image)
        image = if (contrast != null){
            image.contrast(contrast)
        }else{
            image.autocontrast()
        }
        return image
    }

    /**
     * Crop the margin of the image
     * @param image [ImmutableImage] the image you want to crop the margin
     * @return [ImmutableImage] the cropped image
     */
    private fun cropMargin(image: ImmutableImage) : ImmutableImage{
        val backgroundColor = getBackgroundColor(image)
        val mask = image.applyMinFilter(2).filter(GaussianBlurFilter(5))
        val pixelsExtractor = { r: Rectangle -> mask.pixels(r.x, r.y, r.width, r.height) }
        val tolerance = 75
        val x1 = AutocropOps.scanright(backgroundColor, image.height, image.width, 0, pixelsExtractor, tolerance)
        val x2 = AutocropOps.scanleft(backgroundColor, image.height, image.width - 1, pixelsExtractor, tolerance)
        val y1 = AutocropOps.scandown(backgroundColor, image.height, image.width, 0, pixelsExtractor, tolerance)
        val y2 = AutocropOps.scanup(backgroundColor, image.width, image.height - 1, pixelsExtractor, tolerance)
        return try {
            image.subimage(x1, y1, x2 - x1, y2 - y1).let {
                if(it.height < 5 || it.width < 5){
                    image
                }else{
                    it
                }
            }
        }catch (e: RuntimeException){
            image
        }
    }

    /**
     * Greyscale and dither the passed image using the Floyd-Steinberg Algorithm
     * @param image [ImmutableImage] The image to transform
     * @return [ImmutableImage] The transformed image
     */
    private fun greyscaleAndDitherImage(image: ImmutableImage): ImmutableImage {
        for (y in image.height-1 downTo 0){
            for (x in image.width-1 downTo 0){
                val oldColor = image.color(x, y).toGrayscale().gray
                val newColor = deviceInformation.palette.nearestColor(oldColor)
                image.setColor(x, y, getGrayscaleColor(newColor))
                val quantizationError = oldColor-newColor
                if(x+1 < image.width) {
                    image.setColor(
                        x + 1,
                        y,
                        getGrayscaleColor(image.color(x + 1, y).toGrayscale().gray + ((7.0 / 16.0) * quantizationError).toInt())
                    )
                }
                if(y+1 < image.height) {
                    image.setColor(
                        x,
                        y + 1,
                        getGrayscaleColor(image.color(x, y + 1).toGrayscale().gray + ((5.0 / 16.0) * quantizationError).toInt())
                    )
                }
                if (x-1 >= 0 && y+1 < image.height) {
                    image.setColor(
                        x - 1,
                        y + 1,
                        getGrayscaleColor(image.color(x - 1, y + 1).toGrayscale().gray + ((3.0 / 16.0) * quantizationError).toInt())
                    )
                }
                if (x+1 < image.width && y+1 < image.height) {
                    image.setColor(
                        x + 1,
                        y + 1,
                        getGrayscaleColor(image.color(x + 1, y + 1).toGrayscale().gray + ((1.0 / 16.0) * quantizationError).toInt())
                    )
                }
            }
        }
        return image
    }

    /**
     * Resize the image to the device screen size with the passed modes and background color
     * @param image [ImmutableImage] The image that has to be resized
     * @param mode [ResizeModes] The resize mode
     * @param backgroundColor [BackgroundColors] The background color
     * @return [ImmutableImage] The resized image
     */
    private fun resizeImage(
        image: ImmutableImage,
        mode: ResizeModes,
        backgroundColor: BackgroundColors
    ): ImmutableImage {
        val bgColor = backgroundColor.color ?: getBackgroundColor(image)
        val method = when {
            image.width <= deviceInformation.resolution.width && image.height <= deviceInformation.resolution.height -> ScaleMethod.Bicubic
            else -> ScaleMethod.Lanczos3
        }
        val resizedImage = when (mode) {
            ResizeModes.STRETCH -> image.scaleTo(
                deviceInformation.resolution.width,
                deviceInformation.resolution.height,
                method
            )
            ResizeModes.UPSCALE -> image.fit(
                deviceInformation.resolution.width,
                deviceInformation.resolution.height,
                bgColor,
                method,
                Position.Center
            )
            ResizeModes.NOTHING -> if (image.width >= deviceInformation.resolution.width && image.height >= deviceInformation.resolution.height) {
                image.fit(
                    deviceInformation.resolution.width,
                    deviceInformation.resolution.height,
                    bgColor,
                    method,
                    Position.Center
                )
            } else {
                image.resizeTo(deviceInformation.resolution.width, deviceInformation.resolution.height, bgColor)
            }
        }
        return if (backgroundColor == BackgroundColors.NONE){
            try {
                resizedImage.autocrop(getBackgroundColor(image))
            }catch (e: Exception){
                resizedImage
            }
        }else{
            resizedImage
        }
    }

    /**
     * Get the default background color for the passed image
     * @param image [ImmutableImage] The image you want the default background
     * @return [BackgroundColors] The background color
     */
    private fun getBackgroundColor(image: ImmutableImage): Color {
        val bwImage = image.map {
            return@map if (it.toColor().toGrayscale().gray > 128) {
                Color.WHITE
            } else {
                Color.BLACK
            }
        }
        val histogram = bwImage.getHistogram()
        return if (histogram[255] > histogram[0]) {
            Color.WHITE
        } else {
            Color.BLACK
        }
    }

    /**
     * Clean all the file in the temp directory
     * Note: this must be the last function called! After this function, the instance of ImageProcessor on which this function is called become unusable
     */
    fun cleanUp() = temp.toFile().deleteRecursively()

    companion object {
        /**
         * Create an [RGBColor] with the passed gray value
         * @param gray [Int] the gray value
         * @return [RGBColor] the color
         */
        fun getGrayscaleColor(gray: Int) = RGBColor(gray, gray, gray)
    }
}
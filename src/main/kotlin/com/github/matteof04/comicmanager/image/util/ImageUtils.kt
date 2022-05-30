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

package com.github.matteof04.comicmanager.image.util

import com.github.matteof04.comicmanager.util.minOrNSEE
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import java.awt.Color

/**
 * Automatically adjust the image contrast
 * @return [ImmutableImage] The contrast adjusted image
 */
fun ImmutableImage.autocontrast(): ImmutableImage{
    val histogram = getHistogram()
    var minimumGray = 255
    var maximumGray = 0
    histogram.forEachIndexed { gray, occurrence ->
        if (occurrence != 0){
            if (gray < minimumGray){
                minimumGray = gray
            }
            if (gray > maximumGray){
                maximumGray = gray
            }
        }
    }
    val contrast = 255.0/(maximumGray - minimumGray)
    val intensity = -minimumGray*contrast
    return map { pixel ->
        val color = (((pixel.toColor().toGrayscale().gray * contrast) + intensity)).toInt()
        return@map Color(color,color,color)
    }
}

/**
 * Get the image histogram for the passed grayscale image
 * @return [IntArray] The histogram as integer array
 */
fun ImmutableImage.getHistogram(): IntArray {
    val histogram = IntArray(256)
    this.pixels().forEach {
        histogram[it.toColor().toGrayscale().gray]++
    }
    return histogram
}

/**
 * Apply a min filter to the returned [ImmutableImage]
 * @param radius [Int] The radius of the area for the min filter
 * @return [ImmutableImage]
 */
fun ImmutableImage.applyMinFilter(radius: Int): ImmutableImage {
    val newImage = copy()
    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixels = ArrayList<Int>()
            for (yRadius in (y-radius).coerceAtLeast(0)..(y+radius).coerceAtMost(height-1)) {
                for (xRadius in (x-radius).coerceAtLeast(0)..(x+radius).coerceAtMost(width-1)) {
                    pixels.add(color(xRadius, yRadius).toGrayscale().gray)
                }
            }
            val minColor = pixels.minOrNSEE()
            newImage.setColor(x, y, RGBColor(minColor, minColor, minColor))
        }
    }
    return newImage
}
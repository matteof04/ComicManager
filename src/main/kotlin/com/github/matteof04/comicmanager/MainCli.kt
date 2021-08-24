/*
 * Copyright (C) 2021 Matteo Franceschini <matteof5730@gmail.com>
 *
 * This file is part of ComicManager.
 * ComicManager is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ComicManager is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with ComicManager.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.matteof04.comicmanager

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.github.matteof04.comicmanager.devices.CustomDeviceInformation
import com.github.matteof04.comicmanager.devices.DevicesInformations
import com.github.matteof04.comicmanager.devices.util.Resolution
import com.github.matteof04.comicmanager.formats.*
import com.github.matteof04.comicmanager.formats.util.Chapter
import com.github.matteof04.comicmanager.formats.util.PageProgressionDirections
import com.github.matteof04.comicmanager.image.ImageProcessor
import com.github.matteof04.comicmanager.image.util.BackgroundColors
import com.github.matteof04.comicmanager.image.util.ResizeModes
import com.github.matteof04.comicmanager.image.util.SplitModes
import com.github.matteof04.comicmanager.image.util.isImage
import com.github.matteof04.comicmanager.util.*
import kotlin.io.path.*

fun mainCli(args: Array<String>){
    val parser = ArgParser("ComicManager")
    val cliDevice by parser.option(ArgType.Choice<DevicesInformations>(), "device", "d", "The device you want to CM prepare image for ~ Note: if custom height and width are set this will be ignored")
    val customHeight by parser.option(ArgType.Int, "height", description = "The height of your device in portrait mode (use this if your device is not a supported device)").default(0)
    val customWidth by parser.option(ArgType.Int, "width", description = "The width of your device in portrait mode (use this if your device is not a supported device)").default(0)
    val cliFormat by parser.option(ArgType.Choice<Formats>(), "format", "f", "The format you want (if none is provided, CM choose the better format based on your device)")
    val pageProgressionDirection by parser.option(ArgType.Choice<PageProgressionDirections>(), "page-progression-direction", "ppd", "Select page progression direction").default(PageProgressionDirections.LEFT_TO_RIGHT)
    val backgroundColor by parser.option(ArgType.Choice<BackgroundColors>(), "background-color", "bg", "Background color").default(BackgroundColors.NONE)
    val resizeMode by parser.option(ArgType.Choice<ResizeModes>(), "resize-mode", "rm", "Choose resize mode").default(
        ResizeModes.UPSCALE)
    val splitMode by parser.option(ArgType.Choice<SplitModes>(), "split-mode", "sm", "Choose split mode").default(SplitModes.SPLIT)
    val cliContrast by parser.option(ArgType.Double, "contrast", "c", "Manually adjust the image contrast").default(1.0)
    val disableAutocontrast by parser.option(ArgType.Boolean, "no-autocontrast", "noac", "Disable autocontrast ~ Note: if autocontrast is enabled contrast passed value is ignored").default(false)
    val author by parser.option(ArgType.String, "author", "a", "Manually set the author").default("ComicManager")
    val cliOutput by parser.option(ArgType.String, "output", "o", "Output filename without extension")
    val input by parser.option(ArgType.String, "input", "i", "Input dir").required()
    val license by parser.option(ArgType.Boolean, "license", "l", "Show the license").default(false)
    parser.parse(args)
    if (license){
        println(LICENSE)
        return
    }
    val contrast = if (disableAutocontrast){
        cliContrast
    }else{
        null
    }
    val tmpDevice = cliDevice
    val device = if(customHeight == 0 && customWidth == 0 && tmpDevice != null){
        tmpDevice
    }else{
        CustomDeviceInformation(Resolution(customWidth, customHeight))
    }
    var format = cliFormat
    var output = cliOutput
    if(!Path(input).isDirectory()){
        println("Input is not a valid directory")
        return
    }else{
        if (Path(input).listDirectoryEntries().isEmpty()){
            println("Input dir is empty")
            return
        }
    }
    if (format == null){
        format = device.formats.first()
    }
    if (output == null){
        output = Path(input).resolveSibling(Path(input).name + format.extension).toString()
    }
    val processor = ImageProcessor(device)
    val inputDir = Path(input)
    val outputFile = when(format){
        Formats.EPUB -> EPUB(Path(output), Path(output).nameWithoutExtension, author, pageProgressionDirection, device)
        Formats.CBZ -> CBZ(Path(output), Path(output).nameWithoutExtension, author, pageProgressionDirection, device)
        Formats.KEPUB -> KEPUB(Path(output), Path(output).nameWithoutExtension.replace(".kepub", ""), author, pageProgressionDirection, device)
        Formats.MOBI -> MOBI(Path(output), Path(output).nameWithoutExtension, author, pageProgressionDirection, device)
    }
    val directoryMode = inputDir.containsDirectories()
    lateinit var coverImage : Panel
    if (directoryMode){
        runBlocking {
            inputDir.listDirectoryEntries().sorted().forEach { entry ->
                if (entry.isDirectory()){
                    val pages = ArrayList<Panel>()
                    coroutineScope {
                        entry.listDirectoryEntries().sorted().forEach {
                            launch(Dispatchers.IO) {
                                if (it.isImage()) {
                                    processor.preparePanel(
                                        it,
                                        resizeMode,
                                        splitMode,
                                        backgroundColor,
                                        pageProgressionDirection,
                                        contrast
                                    ).forEach {
                                        pages.add(it)
                                    }
                                }
                            }
                        }
                    }
                    outputFile.addChapter(Chapter(entry.name.padStart(4, '0'), pages))
                    println("Chapter ${entry.name.padStart(4, '0')} created!")
                }
            }
        }
        coverImage = processor.preparePanel(
            inputDir.listDirectoryEntries().minOrNSEE().listDirectoryEntries().minOrNSEE(),
            resizeMode,
            SplitModes.ROTATE,
            backgroundColor,
            pageProgressionDirection,
            contrast
        ).first()
        println("Cover created!")
    }else{
        runBlocking {
            val pages = ArrayList<Panel>()
            coroutineScope {
                inputDir.listDirectoryEntries().sorted().forEach { entry ->
                    if (!entry.isDirectory()){
                        launch(Dispatchers.IO) {
                            if(entry.isImage()) {
                                processor.preparePanel(
                                    entry,
                                    resizeMode,
                                    splitMode,
                                    backgroundColor,
                                    pageProgressionDirection,
                                    contrast
                                ).forEach {
                                    pages.add(it)
                                }
                            }
                        }
                    }
                }
            }
            outputFile.addChapter(Chapter(inputDir.name, pages))
        }
        coverImage = processor.preparePanel(
            inputDir.listDirectoryEntries().minOrNSEE(),
            resizeMode,
            SplitModes.ROTATE,
            backgroundColor,
            pageProgressionDirection,
            contrast
        ).first()
        println("Cover created!")
    }
    outputFile.build(coverImage)
    println("File built!")
    processor.cleanUp()
    println("Image temporary directory cleaned!")
}
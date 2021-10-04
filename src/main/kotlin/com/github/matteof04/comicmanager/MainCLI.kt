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

import com.github.matteof04.comicmanager.bookcreator.BookCreator
import com.github.matteof04.comicmanager.bookcreator.BookOptions
import com.github.matteof04.comicmanager.devices.CustomDeviceInformation
import com.github.matteof04.comicmanager.devices.DevicesInformations
import com.github.matteof04.comicmanager.devices.util.Resolution
import com.github.matteof04.comicmanager.formats.Formats
import com.github.matteof04.comicmanager.formats.util.PageProgressionDirections
import com.github.matteof04.comicmanager.image.util.BackgroundColors
import com.github.matteof04.comicmanager.image.util.ResizeModes
import com.github.matteof04.comicmanager.image.util.SplitModes
import com.github.matteof04.comicmanager.util.LICENSE
import com.github.matteof04.comicmanager.util.VolumeSplitter
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.io.path.*

fun mainCli(args: Array<String>){
    val parser = ArgParser("ComicManager")
    val cliDevice by parser.option(ArgType.Choice<DevicesInformations>(), "device", "d", "The device you want to CM prepare image for ~ Note: if custom height and width are set this will be ignored")
    val customHeight by parser.option(ArgType.Int, "height", description = "The height of your device in portrait mode (use this if your device is not a supported device)").default(0)
    val customWidth by parser.option(ArgType.Int, "width", description = "The width of your device in portrait mode (use this if your device is not a supported device)").default(0)
    val cliFormat by parser.option(ArgType.Choice<Formats>(), "format", "f", "The format you want (if none is provided, CM choose the better format based on your device)")
    val pageProgressionDirection by parser.option(ArgType.Choice<PageProgressionDirections>(), "page-progression-direction", "ppd", "Select page progression direction").default(PageProgressionDirections.LEFT_TO_RIGHT)
    val backgroundColor by parser.option(ArgType.Choice<BackgroundColors>(), "background-color", "bg", "Background color").default(BackgroundColors.NONE)
    val resizeMode by parser.option(ArgType.Choice<ResizeModes>(), "resize-mode", "rm", "Choose resize mode").default(ResizeModes.UPSCALE)
    val splitMode by parser.option(ArgType.Choice<SplitModes>(), "split-mode", "sm", "Choose split mode").default(SplitModes.SPLIT)
    val cliContrast by parser.option(ArgType.Double, "contrast", "c", "Manually adjust the image contrast").default(1.0)
    val disableAutocontrast by parser.option(ArgType.Boolean, "no-autocontrast", "noac", "Disable autocontrast ~ Note: if autocontrast is enabled contrast passed value is ignored").default(false)
    val author by parser.option(ArgType.String, "author", "a", "Manually set the author").default("ComicManager")
    val cliSplitVolumes by parser.option(ArgType.Int, "split-volumes", "sv", "Split the input directory in multiple files containing the specified number of chapters")
    val cliOutput by parser.option(ArgType.String, "output", "o", "Output filename without extension")
    val input by parser.option(ArgType.String, "input", "i", "Input dir").required()
    val license by parser.option(ArgType.Boolean, "license", "l", "Show the license").default(false)
    val sync by parser.option(ArgType.Boolean, "sync", "s", "Elaborate the page synchronously").default(false)
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
    val splitVolumes = cliSplitVolumes
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
    val bookCreator = BookCreator { println(it) }
    runBlocking(Dispatchers.IO) {
        var splitter: VolumeSplitter? = null
        val bookOptions = if(splitVolumes != null){
            splitter = VolumeSplitter()
            splitter.split(Path(input).name, Path(input), splitVolumes).listDirectoryEntries().map {
                BookOptions(device, format, pageProgressionDirection, backgroundColor, resizeMode, splitMode, contrast, author,
                    Path(input).resolveSibling("${it.name}${format.extension}"), it)
            }
        }else{
            listOf(BookOptions(device, format, pageProgressionDirection, backgroundColor, resizeMode, splitMode, contrast, author,
                Path(output), Path(input)))
        }
        bookOptions.forEach {
            bookCreator.create(it, sync)
            System.gc()
        }
        splitter?.cleanUp()
    }
}
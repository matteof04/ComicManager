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

package com.github.matteof04.comicmanager.bookcreator

import com.github.matteof04.comicmanager.formats.*
import com.github.matteof04.comicmanager.formats.util.Chapter
import com.github.matteof04.comicmanager.image.ImageProcessor
import com.github.matteof04.comicmanager.image.util.SplitModes
import com.github.matteof04.comicmanager.image.util.isImage
import com.github.matteof04.comicmanager.util.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension

@Suppress("BlockingMethodInNonBlockingContext")
class BookCreator(private val sysOutput: (String) -> Unit) {
    suspend fun create(options: BookOptions, sync: Boolean) {
        val processor = ImageProcessor(options.device)
        val inputDir = options.input
        val outputFile = when(options.format){
            Formats.EPUB -> EPUB(options.output, options.output.nameWithoutExtension, options.author, options.pageProgressionDirection, options.device)
            Formats.CBZ -> CBZ(options.output, options.output.nameWithoutExtension, options.author, options.pageProgressionDirection, options.device)
            Formats.KEPUB -> KEPUB(options.output, options.output.nameWithoutExtension.replace(".kepub", ""), options.author, options.pageProgressionDirection, options.device)
        }
        val directoryMode = inputDir.containsDirectories()
        lateinit var coverImage : Panel
        if (directoryMode){
            inputDir.listDirectoryEntries().sorted().forEach { chapterDir ->
                if (chapterDir.isDirectory()) {
                    val pages = ArrayList<Panel>()
                    coroutineScope {
                        chapterDir.listDirectoryEntries().sorted().forEach {
                            launch {
                                if (it.isImage()) {
                                    processor.preparePanel(it, options).forEach {
                                        pages.add(it)
                                    }
                                }
                            }.takeIf { sync }?.join()
                        }
                    }
                    outputFile.addChapter(Chapter(StringHelper.fixString(chapterDir.name), pages))
                    sysOutput("Chapter ${chapterDir.name.replace(Regex("([^0-9])*"), "").padStart(4, '0')} created!")
                }
            }
            coverImage = processor.preparePanel(
                inputDir.listDirectoryEntries().minOrNSEE().listDirectoryEntries().minOrNSEE(),
                options.copy(splitMode = SplitModes.ROTATE)
            ).first()
            sysOutput("Cover created!")
        }else{
            val pages = ArrayList<Panel>()
            coroutineScope {
                inputDir.listDirectoryEntries().sorted().forEach { entry ->
                    if (!entry.isDirectory()){
                        launch {
                            if(entry.isImage()) {
                                processor.preparePanel(entry, options).forEach {
                                    pages.add(it)
                                }
                            }
                        }.takeIf { sync }?.join()
                    }
                }
            }
            outputFile.addChapter(Chapter(inputDir.name, pages))
            coverImage = processor.preparePanel(
                inputDir.listDirectoryEntries().minOrNSEE(),
                options.copy(splitMode = SplitModes.ROTATE)
            ).first()
            sysOutput("Cover created!")
        }
        outputFile.build(coverImage)
        sysOutput("File built!")
        processor.cleanUp()
        sysOutput("Image temporary directory cleaned!")
    }

    @Deprecated("Renamed", ReplaceWith("BookCreator.create()"), DeprecationLevel.WARNING)
    suspend fun convert(options: BookOptions, sync: Boolean) = create(options, sync)
}
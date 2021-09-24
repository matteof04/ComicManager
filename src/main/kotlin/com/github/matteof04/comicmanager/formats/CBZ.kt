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

package com.github.matteof04.comicmanager.formats

import net.lingala.zip4j.ZipFile
import com.github.matteof04.comicmanager.devices.base.DeviceInformation
import com.github.matteof04.comicmanager.formats.base.Format
import com.github.matteof04.comicmanager.formats.util.Chapter
import com.github.matteof04.comicmanager.formats.util.PageProgressionDirections
import com.github.matteof04.comicmanager.util.Panel
import com.github.matteof04.comicmanager.util.mkdir
import java.nio.file.Path
import kotlin.io.path.*

/**
 * The class for the CBZ format
 * @property temp [Path] the temporary directory where the class operates
 */
class CBZ(
    override val path: Path,
    override val bookTitle: String,
    override val author: String,
    override val pageProgressionDirection: PageProgressionDirections,
    override val deviceInformation: DeviceInformation
) : Format {
    private val temp = createTempDirectory("CBZ_")
    override fun addChapter(chapter: Chapter) {
        val chapterDir = temp.resolve(chapter.title)
        chapterDir.mkdir()
        chapter.pages.forEach {
            it.image.moveTo(chapterDir.resolve(it.image.name))
        }
    }

    override fun build(coverImage: Panel) {
        val zipFile = ZipFile(path.toFile())
        temp.forEachDirectoryEntry {
            if (it.isDirectory()){
                zipFile.addFolder(it.toFile())
            }
        }
        temp.toFile().deleteRecursively()
    }
}
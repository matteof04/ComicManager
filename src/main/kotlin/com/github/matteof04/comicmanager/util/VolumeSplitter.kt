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

package com.github.matteof04.comicmanager.util

import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

class VolumeSplitter {
    private val temp = createTempDirectory("VS")
    fun split(title: String, input: Path, chaptersPerVolume: Int, recovery: Recovery) : Path{
        val volumes = input.listDirectoryEntries().sortedBy { StringHelper.fixString(it.name) }.filter { !recovery.doneChaptersTitles.contains(it.name) }
        val initialIndex = recovery.doneChaptersTitles.chunked(chaptersPerVolume).size
        volumes.chunked(chaptersPerVolume).forEachIndexed { index, volume ->
            volume.forEach {
                it.toFile().copyRecursively(temp.resolve("$title - Volume ${index+initialIndex+1}/${it.name}").toFile())
            }
        }
        return temp
    }
    fun cleanUp() = temp.toFile().deleteRecursively()
}
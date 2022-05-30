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

package com.github.matteof04.comicmanager.util

import com.github.matteof04.comicmanager.devices.base.DeviceInformation
import com.github.matteof04.comicmanager.formats.Formats
import com.github.matteof04.comicmanager.formats.util.PageProgressionDirections
import com.github.matteof04.comicmanager.image.util.BackgroundColors
import com.github.matteof04.comicmanager.image.util.ResizeModes
import com.github.matteof04.comicmanager.image.util.SplitModes
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists

@Serializable
data class Recovery(
    val doneChaptersTitles: MutableList<String> = mutableListOf(),
    val chaptersPerVolume: Int? = null
){
    fun update(path: Path){
        path.toFile().writeText(Json.encodeToString(this))
    }
    companion object{
        fun createFromPath(path: Path?, chaptersPerVolume: Int? = null) = path?.toFile()?.readText()?.runCatching { Json.decodeFromString<Recovery>(this) }?.getOrNull() ?: Recovery(chaptersPerVolume = chaptersPerVolume)
        fun getFile(path: Path) = path.runCatching { this.createFile() }.getOrDefault(path)
        fun cleanUp(path: Path) = path.deleteIfExists()
    }
}

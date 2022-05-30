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

import com.github.matteof04.comicmanager.devices.base.DeviceInformation
import com.github.matteof04.comicmanager.formats.Formats
import com.github.matteof04.comicmanager.formats.util.PageProgressionDirections
import com.github.matteof04.comicmanager.image.util.BackgroundColors
import com.github.matteof04.comicmanager.image.util.ResizeModes
import com.github.matteof04.comicmanager.image.util.SplitModes
import java.nio.file.Path

/**
 * All the options for the panel preparation wrapped in an object
 */
data class BookOptions(
    val device: DeviceInformation,
    val format: Formats,
    val pageProgressionDirection: PageProgressionDirections,
    val backgroundColor: BackgroundColors,
    val resizeMode: ResizeModes,
    val splitMode: SplitModes,
    val contrast: Double?,
    val author: String,
    val output: Path,
    val input: Path
)

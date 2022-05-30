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

package com.github.matteof04.comicmanager.formats.base

import com.github.matteof04.comicmanager.devices.base.DeviceInformation
import com.github.matteof04.comicmanager.formats.util.PageProgressionDirections
import com.github.matteof04.comicmanager.formats.util.Chapter
import com.github.matteof04.comicmanager.util.Panel
import java.nio.file.Path

/**
 * Define the standard implementation for every format
 * @property path [Path] The path of the new generated file
 * @property bookTitle [String] The title of the book (if supported)
 * @property author [String] The author of the book (if supported)
 * @property pageProgressionDirection [PageProgressionDirections] The page progression direction (if supported)
 * @property deviceInformation [DeviceInformation] The device
 */
interface Format {
    val path: Path
    val bookTitle: String
    val author: String
    val pageProgressionDirection: PageProgressionDirections
    val deviceInformation: DeviceInformation

    /**
     * Add a chapter to the book
     * @param chapter [Chapter] The chapter to add
     */
    fun addChapter(chapter: Chapter)

    /**
     * Build the book file
     * @param coverImage [Panel] The cover image (if supported)
     */
    fun build(coverImage: Panel)
}


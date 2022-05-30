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

/**
 * Modes to use to determine how to resize the image
 * @property STRETCH Stretch the image to perfectly fit the screen
 * @property UPSCALE Scale the image to fit the screen, maintaining the aspect ratio. If the image is smaller than the screen size, it will be upscaled
 * @property NOTHING Scale the image if it's bigger than the screen size, maintaining the aspect ratio. If the image is smaller than the screen size, nothing will be done
 */
enum class ResizeModes {
    STRETCH,
    UPSCALE,
    NOTHING
}
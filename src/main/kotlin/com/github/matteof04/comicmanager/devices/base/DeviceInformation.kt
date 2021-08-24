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

package com.github.matteof04.comicmanager.devices.base

import com.github.matteof04.comicmanager.devices.util.Palette
import com.github.matteof04.comicmanager.devices.util.Resolution
import com.github.matteof04.comicmanager.formats.Formats

/**
 * Define the standard implementation for every device
 * @property completeName [String] The device complete name
 * @property resolution [Resolution] The device resolution in portrait mode
 * @property palette [Palette] The device supported color palette
 * @property formats [Array] The formats supported by the device
 */
interface DeviceInformation {
    val completeName: String
    val resolution : Resolution
    val palette: Palette
    val supportPanelView: Boolean
    val formats: Array<Formats>
}
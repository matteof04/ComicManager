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

package com.github.matteof04.comicmanager.formats.util

import com.github.matteof04.comicmanager.util.Panel

/**
 * Stores Panel, its page side and its orientation
 * @property panel [Panel] The panel
 * @property pageSide [PageSide] the custom page side of the panel
 * @property orientation [DeviceOrientation] The default device orientation for this panel, [DeviceOrientation.AUTO] by default
 */
data class PaginatedPanel(
    val panel: Panel,
    var pageSide: PageSide,
    val orientation: DeviceOrientation = DeviceOrientation.AUTO
)

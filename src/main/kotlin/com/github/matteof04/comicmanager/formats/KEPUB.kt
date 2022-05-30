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

package com.github.matteof04.comicmanager.formats

import com.github.matteof04.comicmanager.devices.base.DeviceInformation
import com.github.matteof04.comicmanager.formats.util.PageProgressionDirections
import java.nio.file.Path
/**
 * The class for the KEPUB format, a custom Kobo format
 * This class is only a wrapper for the [EPUB] class, because for the feature we need there's no difference between the two format, except for the extension
 */
class KEPUB(path: Path, bookTitle: String, author: String, pageProgressionDirection: PageProgressionDirections, deviceInformation: DeviceInformation) : EPUB(path, bookTitle, author,
    pageProgressionDirection, deviceInformation)
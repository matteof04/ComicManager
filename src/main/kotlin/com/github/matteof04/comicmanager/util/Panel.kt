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

import com.github.matteof04.comicmanager.formats.util.PageProgressionDirections
import java.nio.file.Path

/**
 * Stores a panel
 * @param image [Path] The panel image path
 * @param metadata [PanelMetadata] The panel metadata, null if there's no metadata
 */
data class Panel(val image: Path, val metadata: PanelMetadata? = null){
    companion object {
        /**
         * Return two panel created from the two images passed
         * @param firstImage [Path] The first image for the first panel
         * @param secondImage [Path] The second image for the second panel
         * @return [ArrayList] an array of two panel
         */
        fun getDoublePanel(firstImage: Path, secondImage: Path, pageProgressionDirection: PageProgressionDirections) : ArrayList<Panel> {
            val panels = ArrayList<Panel>()
            when(pageProgressionDirection){
                PageProgressionDirections.LEFT_TO_RIGHT -> {
                    panels.add(Panel(firstImage, PanelMetadata.FIRST_SPLIT))
                    panels.add(Panel(secondImage, PanelMetadata.SECOND_SPLIT))
                }
                PageProgressionDirections.RIGHT_TO_LEFT -> {
                    panels.add(Panel(secondImage, PanelMetadata.FIRST_SPLIT))
                    panels.add(Panel(firstImage, PanelMetadata.SECOND_SPLIT))
                }
            }
            return panels
        }
    }
}

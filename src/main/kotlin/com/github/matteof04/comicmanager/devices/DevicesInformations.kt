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

package com.github.matteof04.comicmanager.devices

import com.github.matteof04.comicmanager.devices.base.DeviceInformation
import com.github.matteof04.comicmanager.devices.util.Palettes
import com.github.matteof04.comicmanager.devices.util.Resolution
import com.github.matteof04.comicmanager.formats.Formats

/**
 * Contains all supported devices
 */
enum class DevicesInformations : DeviceInformation{
    KOBO_FORMA{
        override val completeName = "Kobo Forma"
        override val resolution = Resolution(1440, 1920)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = false
        override val formats = arrayOf(Formats.KEPUB, Formats.EPUB, Formats.CBZ)
    },
    KOBO_CLARA_HD{
        override val completeName = "Kobo Clara HD"
        override val resolution = Resolution(1072, 1448)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = false
        override val formats = arrayOf(Formats.KEPUB, Formats.EPUB, Formats.CBZ)
    },
    KOBO_LIBRA_H20{
        override val completeName = "Kobo Libra H2O"
        override val resolution = Resolution(1264, 1680)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = false
        override val formats = arrayOf(Formats.KEPUB, Formats.EPUB, Formats.CBZ)
    },
    KOBO_MT{
        override val completeName = "Kobo Mini/Touch"
        override val resolution = Resolution(600, 800)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = false
        override val formats = arrayOf(Formats.KEPUB, Formats.EPUB, Formats.CBZ)
    },
    KOBO_GLO{
        override val completeName = "Kobo Glo"
        override val resolution = Resolution(768, 1024)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = false
        override val formats = arrayOf(Formats.KEPUB, Formats.EPUB, Formats.CBZ)
    },
    KOBO_GLO_HD{
        override val completeName = "Kobo Glo HD"
        override val resolution = Resolution(1072, 1448)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = false
        override val formats = arrayOf(Formats.KEPUB, Formats.EPUB, Formats.CBZ)
    },
    KOBO_AURA{
        override val completeName = "Kobo Aura"
        override val resolution = Resolution(758, 1024)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = false
        override val formats = arrayOf(Formats.KEPUB, Formats.EPUB, Formats.CBZ)
    },
    KOBO_AURA_HD{
        override val completeName = "Kobo Aura HD"
        override val resolution = Resolution(1080, 1440)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = false
        override val formats = arrayOf(Formats.KEPUB, Formats.EPUB, Formats.CBZ)
    },
    KOBO_AURA_H2O{
        override val completeName = "Kobo Aura H2O"
        override val resolution = Resolution(1080, 1430)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = false
        override val formats = arrayOf(Formats.KEPUB, Formats.EPUB, Formats.CBZ)
    },
    KOBO_AURA_ONE{
        override val completeName = "Kobo Aura ONE"
        override val resolution = Resolution(1404, 1872)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = false
        override val formats = arrayOf(Formats.KEPUB, Formats.EPUB, Formats.CBZ)
    },
    KINDLE_1{
        override val completeName = "Kindle 1"
        override val resolution = Resolution(600, 670)
        override val palette = Palettes.PALETTE_4
        override val supportPanelView = false
        override val formats = arrayOf(Formats.EPUB, Formats.CBZ)
    },
    KINDLE_2{
        override val completeName = "Kindle 2"
        override val resolution = Resolution(600, 670)
        override val palette = Palettes.PALETTE_15
        override val supportPanelView = false
        override val formats = arrayOf(Formats.EPUB, Formats.CBZ)
    },
    KINDLE_KT{
        override val completeName = "Kindle Keyboard/Touch"
        override val resolution = Resolution(600, 800)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = false
        override val formats = arrayOf(Formats.EPUB, Formats.CBZ)
    },
    KINDLE_DXG{
        override val completeName = "Kindle DX/DXG"
        override val resolution = Resolution(824, 1000)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = false
        override val formats = arrayOf(Formats.EPUB, Formats.CBZ)
    },
    KINDLE_PAPERWHITE_1_2{
        override val completeName = "Kindle Paperwhite 1/2"
        override val resolution = Resolution(758, 1024)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = true
        override val formats = arrayOf(Formats.EPUB, Formats.CBZ)
    },
    KINDLE_PAPERWHITE_3_4{
        override val completeName = "Kindle Paperwhite 3/4"
        override val resolution = Resolution(1072, 1448)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = true
        override val formats = arrayOf(Formats.EPUB, Formats.CBZ)
    },
    KINDLE_VOYAGE{
        override val completeName = "Kindle Voyage"
        override val resolution = Resolution(1072, 1448)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = true
        override val formats = arrayOf(Formats.EPUB, Formats.CBZ)
    },
    KINDLE_OASIS_1{
        override val completeName = "Kindle Oasis"
        override val resolution = Resolution(1072, 1448)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = true
        override val formats = arrayOf(Formats.EPUB, Formats.CBZ)
    },
    KINDLE_OASIS_2_3 {
        override val completeName = "Kindle Oasis 2/3"
        override val resolution = Resolution(1264, 1680)
        override val palette = Palettes.PALETTE_16
        override val supportPanelView = true
        override val formats = arrayOf(Formats.EPUB, Formats.CBZ)
    };
    companion object {
        fun valueOfCompleteName(completeName: String) = values().firstOrNull { it.completeName == completeName }
    }
}
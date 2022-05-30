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

package com.github.matteof04.comicmanager.devices.util

/**
 * Stores the color palette of a device
 * @param palette [Array] The array of colors in RGB notation supported by the device
 */
data class Palette(val palette: Array<Int>?){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Palette

        if (!palette.contentEquals(other.palette)) return false

        return true
    }

    override fun hashCode(): Int {
        return palette.contentHashCode()
    }

    /**
     * Find the nearest color in the palette to the passed one
     * @param color [Int] The color you want to find nearest in the palette
     * @return [Int] The nearest color as integer
     */
    fun nearestColor(color: Int): Int = if(palette != null){
        var nearestColor = palette.first()
        palette.forEach {
            if (it-color < nearestColor-it){
                nearestColor = it
            }
        }
        nearestColor
    }else{
        color
    }
}

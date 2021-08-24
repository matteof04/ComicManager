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

/**
 * Page progression directions for Format
 * @property LEFT_TO_RIGHT The book will be set to left to right reading mode
 * @property RIGHT_TO_LEFT The book will be set to right to left reading mode
 */
enum class PageProgressionDirections {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT;
    override fun toString(): String {
        return when(this){
            LEFT_TO_RIGHT -> "ltr"
            RIGHT_TO_LEFT -> "rtl"
        }
    }

    /**
     * Get the first [PageSide] for this [PageProgressionDirections]
     * @return [PageSide]
     */
    fun getFirstSide() : PageSide{
        return when(this){
            LEFT_TO_RIGHT -> PageSide.LEFT
            RIGHT_TO_LEFT -> PageSide.RIGHT
        }
    }

    /**
     * Get the first image split suffix
     * @return [String] the suffix
     */
    fun getFirstSplitSuffix() : String{
        return when(this){
            LEFT_TO_RIGHT -> "A"
            RIGHT_TO_LEFT -> "B"
        }
    }
    /**
     * Get the second image split suffix
     * @return [String] the suffix
     */
    fun getSecondSplitSuffix() : String{
        return when(this){
            LEFT_TO_RIGHT -> "B"
            RIGHT_TO_LEFT -> "A"
        }
    }
}

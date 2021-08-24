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

package com.github.matteof04.comicmanager.util

/**
 * Return the smallest element or throw a NSEE if there are no elements
 * @throws NoSuchElementException
 * @return [T] The smallest element
 */
fun <T : Comparable<T>> Iterable<T>.minOrNSEE(): T {
    val min = minOrNull()
    if (min != null){
        return min
    }else{
        throw NoSuchElementException()
    }
}
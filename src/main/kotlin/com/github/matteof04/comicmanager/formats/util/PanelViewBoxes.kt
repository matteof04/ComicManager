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

package com.github.matteof04.comicmanager.formats.util

import com.github.matteof04.comicmanager.devices.base.DeviceInformation

/**
 * All the boxes for PanelView
 */
enum class PanelViewBoxes {
    PV_T,
    PV_B,
    PV_L,
    PV_R,
    PV_TL,
    PV_TR,
    PV_BL,
    PV_BR;

    override fun toString(): String {
        return super.toString().replace("_", "-")
    }

    /**
     * Get the CSS code for the passed box
     * @param width [Int] The width of the image
     * @param height [Int] The height of the image
     * @param devicesInformation [DeviceInformation] The device information
     * @return The CSS [String]
     */
    fun getStyle(width: Int, height: Int, devicesInformation: DeviceInformation) :String {
        val widthGap = (devicesInformation.resolution.width - width)/2.0
        val heightGap = (devicesInformation.resolution.height - height)/2.0
        val percentileWidth = (widthGap/devicesInformation.resolution.width*100).toInt()
        val percentileHeight = (heightGap/devicesInformation.resolution.height*100).toInt()
        return when (this) {
            PV_T -> "position:absolute;top:0;left:$percentileWidth%;"
            PV_B -> "position:absolute;bottom:0;left:$percentileWidth%;"
            PV_L -> "position:absolute;left:0;top:$percentileHeight%;"
            PV_R -> "position:absolute;right:0;top:$percentileHeight%;"
            PV_TL -> "position:absolute;left:0;top:0;"
            PV_TR -> "position:absolute;right:0;top:0;"
            PV_BL -> "position:absolute;left:0;bottom:0;"
            PV_BR -> "position:absolute;right:0;bottom:0;"
        }
    }
    companion object {
        /**
         * Panel view top and bottom boxes
         */
        fun getTBBoxes() = arrayListOf(PV_T, PV_B)

        /**
         * Panel view left and right boxes
         */
        fun getLRBoxes() = arrayListOf(PV_L, PV_R)

        /**
         * Panel view 4 boxes
         */
        fun getTBLRBoxes() = arrayListOf(PV_TL, PV_TR, PV_BL, PV_BR)
    }
}
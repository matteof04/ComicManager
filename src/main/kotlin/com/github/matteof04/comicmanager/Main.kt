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

package com.github.matteof04.comicmanager

import com.github.matteof04.comicmanager.updater.Updater
import com.github.matteof04.comicmanager.util.VersionHelper
import kotlin.system.measureTimeMillis

fun main(args: Array<String>){
    println("""
        ComicManager  Copyright (C) 2021  Matteo Franceschini

        This program comes with ABSOLUTELY NO WARRANTY.
        This is free software, and you are welcome to redistribute it
        under certain conditions; type `-l' or '--license' for details.

    """.trimIndent())
    try {
        val updater = Updater(VersionHelper.getVersion())
        if (updater.checkUpdate()){
            println("There's a new version available, click here to download!\n")
            println(updater.getUpdateURL())
        }
    }catch (e:Exception){

    }
    val time = measureTimeMillis {
        mainCli(args)
    } / 1000.0
    println("Execution time: $time")
}

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

package com.github.matteof04.comicmanager.util.ui

import androidx.compose.animation.core.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color

@Composable
fun WorkInProgressIcon(value: Directory.DirectoryStatus, modifier: Modifier = Modifier){
    val infiniteTransition = rememberInfiniteTransition()
    val rotation = infiniteTransition.animateFloat(180F, 0F, infiniteRepeatable(tween(1000, easing = LinearEasing)))
    when(value){
        Directory.DirectoryStatus.DONE -> Icon(Icons.Filled.CheckCircle, "", tint = Color.Green, modifier = modifier)
        Directory.DirectoryStatus.UNDONE -> Icon(Icons.Filled.Sync, "", modifier)
        Directory.DirectoryStatus.PROCESSING -> Icon(Icons.Filled.Sync, "", Modifier.rotate(rotation.value).then(modifier))
    }
}
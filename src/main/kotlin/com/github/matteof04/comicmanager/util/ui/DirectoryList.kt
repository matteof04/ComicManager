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

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.matteof04.comicmanager.PADDING
import com.github.matteof04.comicmanager.util.StringHelper
import kotlin.io.path.name

@Composable
fun DirectoryList(entries: List<Directory>){
    LazyColumn{
        items(entries.sortedBy { StringHelper.fixString(it.path.name) }) {
            Row(Modifier.fillMaxWidth().padding(1.dp).border(1.dp, Color.Black), verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.size(PADDING))
                WorkInProgressIcon(it.status, Modifier.padding(vertical = PADDING.div(2)))
                Spacer(Modifier.size(PADDING.div(4)))
                Text(it.path.name)
            }
        }
    }
}
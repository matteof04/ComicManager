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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize

@Composable
fun OutlinedDropdownMenu(elements: List<String>, text: String, onSelectedText: ((String) -> Unit), label: (@Composable () -> Unit), modifier: Modifier = Modifier){
    val expanded = remember { mutableStateOf(false) }
    val textFieldSize = remember { mutableStateOf(Size.Zero) }
    val icon = if (expanded.value)
        Icons.Filled.ArrowDropUp
    else
        Icons.Filled.ArrowDropDown
    Column(modifier = modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize.value = coordinates.size.toSize()
                },
            label = label,
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { expanded.value = expanded.value.not() })
            },
            readOnly = true
        )
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.width(with(LocalDensity.current){textFieldSize.value.width.toDp()})
        ) {
            elements.forEach { label ->
                DropdownMenuItem(onClick = {
                    onSelectedText(label)
                    expanded.value = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}
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

package com.github.matteof04.comicmanager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.matteof04.comicmanager.bookcreator.BookCreator
import com.github.matteof04.comicmanager.bookcreator.BookOptions
import com.github.matteof04.comicmanager.bookcreator.Result
import com.github.matteof04.comicmanager.devices.CustomDeviceInformation
import com.github.matteof04.comicmanager.devices.DevicesInformations
import com.github.matteof04.comicmanager.devices.util.Resolution
import com.github.matteof04.comicmanager.formats.Formats
import com.github.matteof04.comicmanager.formats.util.PageProgressionDirections
import com.github.matteof04.comicmanager.image.util.BackgroundColors
import com.github.matteof04.comicmanager.image.util.DoublePagesHandlingMethod
import com.github.matteof04.comicmanager.image.util.ResizeModes
import com.github.matteof04.comicmanager.util.Recovery
import com.github.matteof04.comicmanager.util.StringHelper
import com.github.matteof04.comicmanager.util.VolumeSplitter
import com.github.matteof04.comicmanager.util.ui.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

val PADDING = 16.dp

fun mainUi() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComicManager",
        state = rememberWindowState(placement = WindowPlacement.Maximized)
    ) {
        App()
    }
}

@Composable
@Preview
fun App() {
    val mangaMode = remember { mutableStateOf(true) }
    val autocontrast = remember { mutableStateOf(true) }
    val splitMode = remember { mutableStateOf(true) }
    val splitModeValue = remember { mutableStateOf(10) }
    val contrast = remember { mutableStateOf(1.0) }
    val format = remember { mutableStateOf("EPUB") }
    val device = remember { mutableStateOf("Kobo Forma") }
    val customDevice = remember { derivedStateOf { device.value == "Custom device" } }
    val customDeviceWidth = remember { mutableStateOf(0) }
    val customDeviceHeight = remember { mutableStateOf(0) }
    val resizeMode = remember { mutableStateOf("UPSCALE") }
    val backgroundColor = remember { mutableStateOf("AUTO") }
    val doublePageHandlingMethod = remember { mutableStateOf("SPLIT") }
    val inputDir = remember { mutableStateOf(Directory(Path(""))) }
    val directoryEntries = remember { mutableStateListOf<Directory>() }
    val outputDir = remember { mutableStateOf(Path("")) }

    val lockUI = remember { mutableStateOf(false) }
    MaterialTheme {
        Row(Modifier.padding(PADDING)) {
            Column(Modifier.fillMaxHeight().fillMaxWidth(0.5F)) {
                FilePicker({ file ->
                    if(lockUI.value.not()) {
                        inputDir.value = Directory(file.toPath())
                        outputDir.value = file.toPath().parent ?: file.toPath()
                        directoryEntries.clear()
                        inputDir.value.path.listDirectoryEntries().sortedBy { StringHelper.fixString(it.name) }.forEach {
                            if(it.isDirectory()){
                                directoryEntries.add(Directory(it))
                            }
                        }
                    }
                }, inputDir.value.path.toString(), Modifier.fillMaxWidth()) { Text("Input") }
                Spacer(Modifier.size(PADDING))
                FilePicker(
                    {
                        if(lockUI.value.not()) {
                            outputDir.value = it.toPath()
                        }
                    },
                    outputDir.value.toString(),
                    Modifier.fillMaxWidth()
                ) { Text("Output") }
                Spacer(Modifier.size(PADDING))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedDropdownMenu(
                        DevicesInformations.values().map { it.completeName }.plus("Custom device"),
                        device.value,
                        {
                            if(lockUI.value.not()) {
                                device.value = it
                                DevicesInformations.valueOfCompleteName(it)?.let {devicesInformation ->
                                    format.value = devicesInformation.formats.first().name
                                }
                            }
                        },
                        { Text("Device") },
                        Modifier.fillMaxWidth(0.5F)
                    )
                    Spacer(Modifier.size(PADDING))
                    OutlinedDropdownMenu(
                        Formats.values().map { it.name },
                        format.value,
                        {
                            if(lockUI.value.not()) {
                                format.value = it
                            }
                        },
                        { Text("Format") },
                        Modifier.fillMaxWidth(1.0F)
                    )
                }
                Spacer(Modifier.size(PADDING))
                AnimatedVisibility(customDevice.value) {
                    Row {
                        OutlinedTextField(
                            customDeviceWidth.value.toString(),
                            {
                                if(lockUI.value.not()) {
                                    customDeviceWidth.value = it.toIntOrNull() ?: 0
                                }
                            },
                            label = { Text("Width in portrait mode") },
                            modifier = Modifier.fillMaxWidth(0.5F)
                        )
                        Spacer(Modifier.size(PADDING))
                        OutlinedTextField(
                            customDeviceHeight.value.toString(),
                            {
                                if(lockUI.value.not()) {
                                    customDeviceHeight.value = it.toIntOrNull() ?: 0
                                }
                            },
                            label = { Text("Height in portrait mode") },
                            modifier = Modifier.fillMaxWidth(1.0F)
                        )
                    }
                }
                Spacer(Modifier.size(PADDING))
                Row {
                    OutlinedDropdownMenu(
                        ResizeModes.values().map { it.name },
                        resizeMode.value,
                        {
                            if(lockUI.value.not()) {
                                resizeMode.value = it
                            }
                        },
                        { Text("Resize Mode") },
                        Modifier.fillMaxWidth(0.5F)
                    )
                    Spacer(Modifier.size(PADDING))
                    OutlinedDropdownMenu(
                        DoublePagesHandlingMethod.values().map { it.name },
                        doublePageHandlingMethod.value,
                        {
                            if(lockUI.value.not()) {
                                doublePageHandlingMethod.value = it
                            }
                        },
                        { Text("Double Pages Handling Method") },
                        Modifier.fillMaxWidth(1.0F)
                    )
                }
                Spacer(Modifier.size(PADDING))
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextWithCheckbox("Manga mode:", Modifier.fillMaxWidth(0.5F), mangaMode.value) {
                        if(lockUI.value.not()) {
                            mangaMode.value = it
                        }
                    }
                    Spacer(Modifier.size(PADDING))
                    OutlinedDropdownMenu(
                        BackgroundColors.values().map { it.name },
                        backgroundColor.value,
                        {
                            if(lockUI.value.not()) {
                                backgroundColor.value = it
                            }
                        },
                        { Text("Background Color") },
                        Modifier.fillMaxWidth(1.0F)
                    )
                }
                Spacer(Modifier.size(PADDING))
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextWithCheckbox("Split mode:", Modifier.fillMaxWidth(0.5F), splitMode.value) {
                        if(lockUI.value.not()) {
                            splitMode.value = it
                        }
                    }
                    Spacer(Modifier.size(PADDING))
                    AnimatedVisibility(splitMode.value) {
                        OutlinedTextField(
                            splitModeValue.value.toString(),
                            {
                                if(lockUI.value.not()) {
                                    splitModeValue.value = it.toIntOrNull() ?: 10
                                }
                            },
                            modifier = Modifier.fillMaxWidth(1.0F),
                            label = { Text("Split value") })
                    }
                }
                Spacer(Modifier.size(PADDING))
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextWithCheckbox(
                        "Autocontrast:",
                        Modifier.fillMaxWidth(0.5F),
                        autocontrast.value
                    ) {
                        if(lockUI.value.not()) {
                            autocontrast.value = it
                        }
                    }
                    Spacer(Modifier.size(PADDING))
                    AnimatedVisibility(autocontrast.value.not()) {
                        OutlinedTextField(
                            contrast.value.toString(),
                            {
                                if(lockUI.value.not()) {
                                    contrast.value = it.toDoubleOrNull() ?: 1.0
                                }
                            },
                            label = { Text("Contrast") })
                    }
                }
                Spacer(Modifier.size(PADDING))
                Button({
                    if(lockUI.value.not()){
                        lockUI.value = true
                        inputDir.value = inputDir.value.processing()
                        directoryEntries.replaceAll { it.processing() }
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Start conversion")
                }
                LaunchedEffect(lockUI.value) {
                    if(lockUI.value){
                        val pageProgressionDirections = if(mangaMode.value){
                            PageProgressionDirections.LEFT_TO_RIGHT
                        }else{
                            PageProgressionDirections.RIGHT_TO_LEFT
                        }
                        val devicesInformation = if(customDevice.value){
                            CustomDeviceInformation(Resolution(customDeviceWidth.value, customDeviceHeight.value))
                        }else{
                            DevicesInformations.valueOfCompleteName(device.value) ?: DevicesInformations.KOBO_FORMA
                        }
                        val selectedFormat = Formats.valueOf(format.value)
                        val splitter = if(splitMode.value){
                            VolumeSplitter()
                        }else{
                            null
                        }
                        val bookOptions = if(splitter != null){
                            VolumeSplitter().split(inputDir.value.path.name, inputDir.value.path, splitModeValue.value, Recovery(chaptersPerVolume = splitModeValue.value)).listDirectoryEntries().map {
                                BookOptions(
                                    devicesInformation,
                                    selectedFormat,
                                    pageProgressionDirections,
                                    BackgroundColors.valueOf(backgroundColor.value),
                                    ResizeModes.valueOf(resizeMode.value),
                                    DoublePagesHandlingMethod.valueOf(doublePageHandlingMethod.value),
                                    contrast.value,
                                    "ComicManager",
                                    outputDir.value.resolveSibling("${it.name}${selectedFormat.extension}"),
                                    it
                                )
                            }
                        }else{
                            listOf(
                                BookOptions(
                                    devicesInformation,
                                    selectedFormat,
                                    pageProgressionDirections,
                                    BackgroundColors.valueOf(backgroundColor.value),
                                    ResizeModes.valueOf(resizeMode.value),
                                    DoublePagesHandlingMethod.valueOf(doublePageHandlingMethod.value),
                                    contrast.value,
                                    "ComicManager",
                                    outputDir.value.resolve(inputDir.value.path.name + selectedFormat.extension),
                                    inputDir.value.path
                                )
                            )
                        }
                        val bookCreator = BookCreator { result ->
                            when(result.resultType){
                                Result.ResultType.CHAPTER -> {
                                    val doneDir = directoryEntries.firstOrNull { it.path == result.resultPath }
                                    doneDir?.let {
                                        directoryEntries.remove(doneDir)
                                        directoryEntries.add(doneDir.done())
                                    }
                                }
                                Result.ResultType.BOOK -> {
                                    inputDir.value = inputDir.value.done()
                                    lockUI.value = false
                                }
                                Result.ResultType.COVER -> {  }
                            }
                        }
                        launch(Dispatchers.IO) {
                            bookOptions.forEach {
                                bookCreator.create(it, false)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.size(PADDING.times(2)))
            Column(Modifier.fillMaxSize().border(2.dp, Color.Black).padding(PADDING)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(1.dp).border(1.dp, Color.Black).padding(PADDING.div(2))) {
                    Text("Current directory: ${inputDir.value.path}", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(0.95F))
                    WorkInProgressIcon(inputDir.value.status, modifier = Modifier.fillMaxWidth(1.0F))
                }
                DirectoryList(directoryEntries)
            }
        }
    }
}
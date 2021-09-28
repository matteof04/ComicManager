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

package com.github.matteof04.comicmanager.updater

import com.github.matteof04.comicmanager.updater.util.LatestRelease
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import java.net.URL

class Updater(private val version: String) {
    private val updateURL = URL("https://api.github.com/repos/matteof04/ComicManager/releases/latest")
    private val client = HttpClient{
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json { this.ignoreUnknownKeys = true })
        }
    }
    fun checkUpdate() = runBlocking {
        val latestRelease = client.get<LatestRelease>(updateURL)
        return@runBlocking latestRelease.tagName != version && !latestRelease.prerelease
    }
    fun getUpdateURL() = runBlocking {
        val latestRelease = client.get<LatestRelease>(updateURL)
        return@runBlocking latestRelease.htmlUrl
    }
}
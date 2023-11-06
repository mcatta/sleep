/*
 * Copyright 2023 Marco Cattaneo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.marcocattaneo.sleep.playlist.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.marcocattaneo.sleep.core.utils.AbsPresenter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistPresenter @Inject constructor() : AbsPresenter<PlaylistState, PlaylistEvent>() {

    @Composable
    override fun models(events: Flow<PlaylistEvent>): PlaylistState {
        var latestTrackId: String? by remember { mutableStateOf(null) }

        LaunchedEffect(Unit) {
            events.collectLatest { event ->
                latestTrackId = when (event) {
                    is PlaylistEvent.Clear -> null
                    is PlaylistEvent.Update -> event.trackId
                }
            }
        }

        return PlaylistState(currentTrackId = latestTrackId)
    }

}

data class PlaylistState(
    val currentTrackId: String? = null
)

sealed interface PlaylistEvent {
    data class Update(val trackId: String) : PlaylistEvent
    data object Clear : PlaylistEvent
}
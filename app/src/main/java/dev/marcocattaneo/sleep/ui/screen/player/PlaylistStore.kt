/*
 * Copyright 2022 Marco Cattaneo
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

package dev.marcocattaneo.sleep.ui.screen.player

import dev.marcocattaneo.mvi.State
import dev.marcocattaneo.mvi.intent.Action
import dev.marcocattaneo.mvi.store.ChannelStore
import dev.marcocattaneo.sleep.di.scope.CoroutineContextScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistStore @Inject constructor(
    @CoroutineContextScope scope: CoroutineScope
): ChannelStore<PlaylistState>(
    scope = scope,
    initialState = PlaylistState()
)

data class PlaylistState(
    val currentTrackId: String? = null
) : State

sealed interface PlaylistAction : Action {
    data class Update(val trackId: String) : PlaylistAction
    object Clear : PlaylistAction
}
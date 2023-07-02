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

package dev.marcocattaneo.sleep.player.presentation.screen

import dev.marcocattaneo.sleep.core.di.scope.CoroutineContextScope
import dev.mcatta.polpetta.StateStore
import dev.mcatta.polpetta.operators.Action
import dev.mcatta.polpetta.operators.State
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistStateStore @Inject constructor(
    @CoroutineContextScope coroutineScope: CoroutineScope
) : StateStore<PlaylistAction, PlaylistState>(
    initialState = PlaylistState(),
    coroutineScope = coroutineScope,
    reducerFactory = {
        on<PlaylistAction.Update> { action, modifier ->
            modifier.mutate { copy(currentTrackId = action.trackId) }
        }

        on<PlaylistAction.Clear> { _, modifier ->
            modifier.mutate { copy(currentTrackId = null) }
        }
    }
)

data class PlaylistState(
    val currentTrackId: String? = null
) : State

sealed interface PlaylistAction : Action {
    data class Update(val trackId: String) : PlaylistAction
    object Clear : PlaylistAction
}
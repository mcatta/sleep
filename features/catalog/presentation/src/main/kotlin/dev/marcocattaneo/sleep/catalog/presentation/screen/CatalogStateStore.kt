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

package dev.marcocattaneo.sleep.catalog.presentation.screen

import dagger.hilt.android.scopes.ViewModelScoped
import dev.marcocattaneo.sleep.core.di.scope.CoroutineContextScope
import dev.marcocattaneo.sleep.domain.model.MediaFileEntity
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import dev.mcatta.polpetta.StateStore
import dev.mcatta.polpetta.operators.Action
import dev.mcatta.polpetta.operators.State
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ViewModelScoped
class HomeStateStore @Inject constructor(
    @CoroutineContextScope coroutineScope: CoroutineScope,
    private val mediaRepository: MediaRepository
) : StateStore<TracksAction, TracksState, Nothing>(
    initialState = TracksState.Content(),
    coroutineScope = coroutineScope,
    reducerFactory = {
        on<TracksAction.SetLoading, TracksState> { _, modifier ->
            modifier.transform { TracksState.Loading }
        }

        on<TracksAction.LoadTracks, TracksState> { _, modifier ->
            mediaRepository.listMedia().fold(
                ifLeft = { err -> modifier.transform { TracksState.Error(err.message) } },
                ifRight = { list -> modifier.transform { TracksState.Content(list) } }
            )
        }

        on<TracksAction.UpdateSelectedTrack, TracksState.Content> { action, modifier ->
            modifier.mutate { copy(selectedTrackId = action.trackId) }
        }
    }
)

sealed interface TracksState : State {
    object Loading : TracksState
    data class Error(val message: String? = null) : TracksState
    data class Content(
        val mediaFiles: List<MediaFileEntity> = emptyList(),
        val selectedTrackId: String? = null,
    ) : TracksState {
        val homeMediaFile: List<MediaFileEntity>
            get() = mediaFiles.map { it.copy(selected = it.id == selectedTrackId) }
    }
}

sealed interface TracksAction : Action {
    data class UpdateSelectedTrack(val trackId: String?) : TracksAction
    object LoadTracks : TracksAction
    object SetLoading : TracksAction
}
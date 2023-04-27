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

package dev.marcocattaneo.sleep.ui.screen.home

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import dagger.hilt.android.scopes.ViewModelScoped
import dev.marcocattaneo.sleep.domain.model.MediaFileEntity
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@ViewModelScoped
class HomeStateMachine @Inject constructor(
    private val mediaRepository: MediaRepository,
): FlowReduxStateMachine<TracksState, TracksAction>(
    initialState = TracksState.Loading
) {

    init {
        spec {
            inState<TracksState.Loading> {
                onEnter { state ->
                    mediaRepository.listMedia().fold(
                        ifLeft = { err -> state.override { TracksState.Error(err.message) } },
                        ifRight = { list -> state.override { TracksState.Content(list) } }
                    )
                }
            }

            inState<TracksState.Error> {
                on { _: TracksAction.Reload, state: State<TracksState.Error> ->
                    state.override { TracksState.Loading }
                }
            }


            inState {
                on { action: TracksAction.UpdateSelectedTrack, state: State<TracksState.Content> ->
                    state.override { copy(selectedTrackId = action.trackId) }
                }
                on { _: TracksAction.Reload, state: State<TracksState.Content> ->
                    state.override { TracksState.Loading }
                }
            }

            inState {
                on { _: TracksAction.Reload, state: State<TracksState.Error> ->
                    state.override { TracksState.Loading }
                }
            }

        }
    }

}

sealed interface TracksState {
    object Loading: TracksState
    data class Error(val message: String? = null): TracksState
    data class Content(
        val mediaFiles: List<MediaFileEntity> = emptyList(),
        val selectedTrackId: String? = null,
    ): TracksState {
        val homeMediaFile: List<MediaFileEntity>
            get() = mediaFiles.map { it.copy(selected = it.id == selectedTrackId) }
    }
}

sealed interface TracksAction {
    data class UpdateSelectedTrack(val trackId: String?): TracksAction
    object Reload: TracksAction
}
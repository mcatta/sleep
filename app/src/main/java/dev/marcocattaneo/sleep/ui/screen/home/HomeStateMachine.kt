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

import arrow.core.Either
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dagger.hilt.android.scopes.ViewModelScoped
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.model.MediaFile
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
                          ifRight = { list -> state.override { TracksState.Content(list) }}
                      )
                  }
              }
        }
    }

}

sealed interface TracksState {
    object Loading: TracksState
    data class Error(val message: String? = null): TracksState
    data class Content(
        val mediaFiles: List<MediaFile> = emptyList(),
        val selectedTrackId: String? = null,
    ): TracksState {
        val homeMediaFile: List<MediaFile>
            get() = mediaFiles.map { it.copy(selected = it.id == selectedTrackId) }
    }
}

sealed interface TracksAction {
    object ShowLoading: TracksAction
    object CheckAudioList: TracksAction
    data class UpdateSelectedTrack(val trackId: String?): TracksAction
    data class CheckAudioListResult(val result: Either<AppException, List<MediaFile>>): TracksAction
}
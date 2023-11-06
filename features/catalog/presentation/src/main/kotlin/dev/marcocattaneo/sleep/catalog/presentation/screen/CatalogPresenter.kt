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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.marcocattaneo.sleep.core.utils.AbsPresenter
import dev.marcocattaneo.sleep.domain.model.MediaFileEntity
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CatalogPresenter @Inject constructor(
    private val mediaRepository: MediaRepository,
) : AbsPresenter<CatalogState, CatalogEvent>() {

    @Composable
    override fun models(events: Flow<CatalogEvent>): CatalogState {
        val state = remember { mutableStateOf<CatalogState>(CatalogState.Content()) }

        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    is CatalogEvent.LoadTracks -> loadMedia(state)
                    is CatalogEvent.UpdateSelectedTrack -> {
                        state.value = (state.value as? CatalogState.Content)?.copy(
                            selectedTrackId = event.trackId
                        ) ?: CatalogState.Content()
                    }

                }
            }
        }

        return state.value
    }

    private suspend fun loadMedia(state: MutableState<CatalogState>) {
        state.value = CatalogState.Loading
        state.value = mediaRepository.listMedia().fold(
            ifLeft = { err -> CatalogState.Error(err.message ?: "") },
            ifRight = { list -> CatalogState.Content(list) }
        )
    }

}

sealed interface CatalogState {
    data object Loading : CatalogState
    data class Error(val message: String? = null) : CatalogState
    data class Content(
        val mediaFiles: List<MediaFileEntity> = emptyList(),
        val selectedTrackId: String? = null,
    ) : CatalogState {
        val homeMediaFile: List<MediaFileEntity>
            get() = mediaFiles.map { it.copy(selected = it.id == selectedTrackId) }
    }
}

sealed interface CatalogEvent {
    data class UpdateSelectedTrack(val trackId: String?) : CatalogEvent
    data object LoadTracks : CatalogEvent
}
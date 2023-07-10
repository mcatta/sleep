/*
 * Copyright 2021 Marco Cattaneo
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

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.marcocattaneo.sleep.core.utils.AbsStateStoreViewModel
import dev.marcocattaneo.sleep.player.presentation.screen.PlaylistStateStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val playlistStateStore: PlaylistStateStore,
    homeStateStore: HomeStateStore
) : AbsStateStoreViewModel<TracksAction, TracksState, Nothing>(
    stateStore = homeStateStore
) {

    init {
        dispatch(TracksAction.SetLoading)
        dispatch(TracksAction.LoadTracks)

        viewModelScope.launch {
            playlistStateStore.stateFlow.collectLatest { playlistState ->
                if (playlistState.currentTrackId != null) {
                    dispatch(TracksAction.UpdateSelectedTrack(trackId = playlistState.currentTrackId))
                }
            }
        }
    }
}
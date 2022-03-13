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

package dev.marcocattaneo.asmrelax.ui.screen.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.marcocattaneo.asmrelax.domain.model.Path
import dev.marcocattaneo.asmrelax.domain.repository.MediaRepository
import dev.marcocattaneo.asmrelax.ui.player.AudioPlayer
import dev.marcocattaneo.asmrelax.ui.player.AudioPlayerState
import dev.marcocattaneo.asmrelax.ui.screen.RouteKeys
import dev.marcocattaneo.asmrelax.ui.screen.common.StateViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer,
    private val mediaRepository: MediaRepository,
    savedStateHandle: SavedStateHandle
) : StateViewModel<PlayerViewModel.Status>(
    initialState = Status()
) {

    init {
        val urlPath: Path = savedStateHandle.get<String>(RouteKeys.PATH_KEY)!!

        viewModelScope.launch {
            mediaRepository.urlFromPath(urlPath)
                .map(audioPlayer::start)
                .mapLeft { it.printStackTrace() }

            audioPlayer.state().collect {
                when (it) {
                    AudioPlayerState.None -> {}
                    AudioPlayerState.OnInit -> emitState { state -> state.copy(playerStatus = Status.PlayerStatus.Init) }
                    AudioPlayerState.OnPause -> emitState { state -> state.copy(playerStatus = Status.PlayerStatus.Pause) }
                    AudioPlayerState.OnProgress -> emitState { state -> state.copy(playerStatus = Status.PlayerStatus.Playing) }
                    is AudioPlayerState.UpdateDuration -> emitState { state ->
                        state.copy(
                            duration = it.duration
                        )
                    }
                    is AudioPlayerState.UpdatePosition -> emitState { state ->
                        state.copy(
                            position = it.position
                        )
                    }
                    is AudioPlayerState.OnError -> emitState { state ->
                        state.copy(
                            playerStatus = Status.PlayerStatus.Error(
                                it.errorCode
                            )
                        )
                    }
                }
            }
        }
    }

    fun dispose() = audioPlayer.dispose()

    data class Status(
        val duration: Int = 0,
        val position: Int = 0,
        val playerStatus: PlayerStatus = PlayerStatus.Init
    ) {
        sealed interface PlayerStatus {
            object Playing : PlayerStatus
            object Pause : PlayerStatus
            data class Error(val errorCode: Int) : PlayerStatus
            object Init : PlayerStatus
        }
    }
}

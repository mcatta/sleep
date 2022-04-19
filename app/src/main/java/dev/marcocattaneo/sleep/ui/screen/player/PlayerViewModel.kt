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

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.marcocattaneo.sleep.ui.player.AudioPlayer
import dev.marcocattaneo.sleep.ui.player.AudioPlayerState
import dev.marcocattaneo.sleep.ui.screen.common.MviViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer,
    playerStore: PlayerStore,
    playerIntentFactory: PlayerIntentFactory
) : MviViewModel<PlayerState, PlayerAction>(
    store = playerStore,
    intentFactory = playerIntentFactory
) {

    init {
        viewModelScope.launch {
            audioPlayer.state().collect {
                when (it) {
                    AudioPlayerState.OnInit -> listOf(PlayerAction.UpdateStatus(PlayerState.PlayerStatus.Init))
                    AudioPlayerState.OnPause -> listOf(PlayerAction.UpdateStatus(PlayerState.PlayerStatus.Pause))
                    AudioPlayerState.OnStop -> listOf(
                        PlayerAction.StopAfter(null),
                        PlayerAction.UpdateStatus(PlayerState.PlayerStatus.Stop),
                        PlayerAction.UpdateTrack(trackId = null)
                    )

                    is AudioPlayerState.PlayerStatus -> listOf(
                        PlayerAction.UpdateDuration(
                            duration = it.duration,
                            position = it.position,
                            stopAfterMinutes = it.stopAt
                        ),
                        PlayerAction.UpdateStatus(if (it.isPlaying) PlayerState.PlayerStatus.Playing else PlayerState.PlayerStatus.Pause)
                    )
                    is AudioPlayerState.OnError -> listOf(
                        PlayerAction.UpdateStatus(
                            PlayerState.PlayerStatus.Error(it.errorCode)
                        )
                    )

                    AudioPlayerState.Disposed -> listOf(PlayerAction.UpdateStatus(PlayerState.PlayerStatus.Disposed))
                    AudioPlayerState.None -> null
                }?.map(::process)
            }
        }
    }

}

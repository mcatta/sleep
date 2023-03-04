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

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.marcocattaneo.sleep.core.utils.AbsStateMachineViewModel
import dev.marcocattaneo.sleep.player.presentation.AudioPlayer
import dev.marcocattaneo.sleep.player.presentation.state.AudioPlayerEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer,
    playerStateMachine: PlayerStateMachine,
) : AbsStateMachineViewModel<PlayerState, PlayerAction>(
    stateMachine = playerStateMachine
) {

    init {
        viewModelScope.launch {
            audioPlayer.state().collect { playerEvent ->
                when (playerEvent) {
                    AudioPlayerEvent.Stop -> listOf(
                        PlayerAction.StopAfter(null), PlayerAction.Stop
                    )

                    is AudioPlayerEvent.PlayerStatus -> listOf(
                        PlayerAction.UpdateDuration(
                            duration = playerEvent.duration,
                            position = playerEvent.position,
                            stopAfterMinutes = playerEvent.stopAt,
                            playing = playerEvent.isPlaying
                        )
                    )

                    is AudioPlayerEvent.Error -> listOf(
                        PlayerAction.PropagateError(playerEvent.errorCode)
                    )

                    AudioPlayerEvent.Disposed -> null
                    AudioPlayerEvent.Init -> null
                    AudioPlayerEvent.None -> null
                    AudioPlayerEvent.Pause -> null
                }?.map(::dispatch)
            }
        }
    }

}

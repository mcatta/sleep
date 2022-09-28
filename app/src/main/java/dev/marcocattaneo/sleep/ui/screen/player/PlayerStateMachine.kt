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

import android.net.Uri
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import dagger.hilt.android.scopes.ViewModelScoped
import dev.marcocattaneo.sleep.domain.model.MediaFile
import dev.marcocattaneo.sleep.domain.model.Minutes
import dev.marcocattaneo.sleep.domain.model.Seconds
import dev.marcocattaneo.sleep.domain.model.sec
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import dev.marcocattaneo.sleep.ui.player.AudioPlayer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@ViewModelScoped
class PlayerStateMachine @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val audioPlayer: AudioPlayer,
    private val playlistStateMachine: PlaylistStateMachine
) : FlowReduxStateMachine<PlayerState, PlayerAction>(
    initialState = PlayerState.Stop
) {

    init {
        spec {
            inState {
                on { _: PlayerAction.Play, state: State<PlayerState.Pause> ->
                    audioPlayer.play()

                    state.override {
                        PlayerState.Playing(
                            duration = this.duration,
                            position = this.position,
                            stopTimer = this.stopTimer
                        )
                    }
                }
            }

            // Any state
            inState {
                on { action: PlayerAction.UpdateDuration, state: State<PlayerState> ->
                    state.override {
                        PlayerState.Playing(
                            duration = action.duration,
                            position = action.position,
                            stopTimer = action.stopAfterMinutes
                        )
                    }
                }
                on { _: PlayerAction.Stop, state: State<PlayerState> ->
                    state.override { PlayerState.Stop }
                }
                on { action: PlayerAction.StartPlaying, state: State<PlayerState> ->
                    audioPlayer.stop()

                    mediaRepository.urlFromPath(action.mediaFile.path).fold(
                        ifLeft = { state.override { PlayerState.Error(500) } },
                        ifRight = {
                            playlistStateMachine.dispatch(PlaylistAction.Update(trackId = action.mediaFile.id))

                            audioPlayer.start(Uri.parse(it))
                            state.noChange()
                        }
                    )
                }
            }

            inState {
                on { _: PlayerAction.Pause, state: State<PlayerState.Playing> ->
                    audioPlayer.pause()

                    state.override {
                        PlayerState.Pause(
                            duration = this.duration,
                            position = this.position,
                            stopTimer = this.stopTimer
                        )
                    }
                }
                on { action: PlayerAction.StopAfter, state: State<PlayerState.Playing> ->
                    val newTimer: Minutes? = if (state.snapshot.stopTimer != action.minutes) {
                        action.minutes
                    } else null

                    audioPlayer.stopAfter(newTimer)
                    state.override { copy(stopTimer = newTimer) }
                }
                on { action: PlayerAction.SeekTo, state: State<PlayerState.Playing> ->
                    audioPlayer.seekTo(action.position)
                    state.noChange()
                }

                on { _: PlayerAction.ForwardOf, state: State<PlayerState.Playing> ->
                    audioPlayer.forwardOf(30.sec)
                    state.noChange()
                }

                on { _: PlayerAction.ReplayOf, state: State<PlayerState.Playing> ->
                    audioPlayer.replayOf(30.sec)
                    state.noChange()
                }
            }
        }
    }
}

sealed interface PlayerState {
    object Disposed : PlayerState
    object Init : PlayerState
    data class Playing(
        override val duration: Seconds = 0.sec,
        override val position: Seconds = 0.sec,
        override val stopTimer: Minutes? = null
    ) : CommonPlayingState

    data class Pause(
        override val duration: Seconds = 0.sec,
        override val position: Seconds = 0.sec,
        override val stopTimer: Minutes? = null
    ) : CommonPlayingState

    interface CommonPlayingState : PlayerState {
        val duration: Seconds
        val position: Seconds
        val stopTimer: Minutes?
    }

    object Stop : PlayerState
    class Error(val errorCode: Int) : PlayerState
}

sealed interface PlayerAction {
    data class StartPlaying(val mediaFile: MediaFile) : PlayerAction

    //data class UpdateStatus(val status: PlayerState.PlayerStatus) : PlayerAction
    data class UpdateDuration(
        val duration: Seconds,
        val position: Seconds,
        val stopAfterMinutes: Minutes?,
        val playing: Boolean = false
    ) : PlayerAction

    class PropagateError(val errorCode: Int) : PlayerAction

    object Play : PlayerAction
    object Pause : PlayerAction
    object Stop : PlayerAction

    object ReplayOf : PlayerAction
    object ForwardOf : PlayerAction
    class SeekTo(val position: Seconds) : PlayerAction
    class StopAfter(val minutes: Minutes?) : PlayerAction
}
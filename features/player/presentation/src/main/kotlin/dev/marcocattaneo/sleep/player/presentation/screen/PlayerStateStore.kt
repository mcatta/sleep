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
import dev.marcocattaneo.sleep.domain.model.MediaFileEntity
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import dev.marcocattaneo.sleep.player.presentation.AudioPlayer
import dev.marcocattaneo.sleep.playlist.presentation.PlaylistEvent
import dev.marcocattaneo.sleep.playlist.presentation.PlaylistPresenter
import dev.mcatta.polpetta.StateStore
import dev.mcatta.polpetta.operators.Action
import dev.mcatta.polpetta.operators.State
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Singleton
class PlayerStateStore @Inject constructor(
    @CoroutineContextScope coroutineScope: CoroutineScope,
    private val audioPlayer: AudioPlayer,
    private val mediaRepository: MediaRepository,
    private val playlistPresenter: PlaylistPresenter
) : StateStore<PlayerAction, PlayerState, Nothing>(
    coroutineScope = coroutineScope,
    initialState = PlayerState.Idle,
    reducerFactory = {

        on<PlayerAction.StartPlaying, PlayerState> { action, state ->
            // stop previous
            audioPlayer.stop()

            mediaRepository.urlFromId(action.mediaFile.id).fold(
                ifLeft = { state.transform { PlayerState.Error(500) } },
                ifRight = {
                    // Update PlayList
                    //TODO need to be fixed
                    //playlistPresenter.dispatchAction(PlaylistEvent.Update(trackId = action.mediaFile.id))

                    // Start Audio Player with url
                    audioPlayer.start(it, action.mediaFile.name, action.mediaFile.description)

                    state.transform {
                        PlayerState.Ready(
                            duration = 0.seconds,
                            position = 0.seconds,
                            stopTimer = null,
                            trackTitle = "",
                            status = PlayerState.Status.Playing
                        )
                    }
                }
            )
        }

        on<PlayerAction.Play, PlayerState.Ready> { _, state ->
            audioPlayer.play()

            state.mutate { copy(status = PlayerState.Status.Playing) }
        }

        on<PlayerAction.UpdatePlayerStatus, PlayerState> { action, state ->
            state.transform {
                // Check if the stop timer is passed

                if (this is PlayerState.Ready) {
                    val mustStop =
                        stopDate?.let { stopDate -> System.currentTimeMillis() > stopDate } ?: false
                    if (mustStop) {
                        audioPlayer.stop()
                        PlayerState.Idle
                    } else {
                        copy(
                            duration = action.duration,
                            position = action.position,
                            trackTitle = action.trackTitle,
                            status = PlayerState.Status.Playing,
                            stopDate = stopDate,
                            stopTimer = stopTimer
                        )
                    }
                } else {
                    PlayerState.Ready(
                        duration = action.duration,
                        position = action.position,
                        trackTitle = action.trackTitle,
                        status = PlayerState.Status.Playing
                    )
                }
            }
        }

        on<PlayerAction.Stop, PlayerState> { _, state ->
            audioPlayer.stop()
            state.transform { PlayerState.Idle }
        }

        on<PlayerAction.Pause, PlayerState.Ready> { _, state ->
            audioPlayer.pause()

            state.transform { copy(status = PlayerState.Status.Paused) }
        }

        on<PlayerAction.StopAfter, PlayerState.Ready> { action, state ->
            state.mutate {
                val newTimer: Duration? = if (this.stopTimer != action.minutes) {
                    action.minutes
                } else null

                copy(
                    stopTimer = newTimer,
                    stopDate = newTimer?.let {
                        System.currentTimeMillis().plus(it.inWholeMilliseconds)
                    }
                )
            }
        }

        on<PlayerAction.SeekTo, PlayerState.Ready> { action, state ->
            audioPlayer.seekTo(action.position)
            state.nothing()
        }

        on<PlayerAction.ForwardOf, PlayerState.Ready> { _, state ->
            audioPlayer.forwardOf(30.seconds)
            state.nothing()
        }

        on<PlayerAction.ReplayOf, PlayerState.Ready> { _, state ->
            audioPlayer.replayOf(30.seconds)
            state.nothing()
        }

        on<PlayerAction.PropagateError, PlayerState> { action, state ->
            state.transform { PlayerState.Error(action.errorCode) }
        }
    }
)

sealed interface PlayerState : State {
    object Idle : PlayerState

    data class Ready(
        val duration: Duration = 0.seconds,
        val position: Duration = 0.seconds,
        val stopTimer: Duration? = null,
        val stopDate: Long? = null,
        val trackTitle: String,
        val status: Status
    ) : PlayerState

    class Error(val errorCode: Int) : PlayerState

    enum class Status { Playing, Paused }
}

sealed interface PlayerAction : Action {
    data class StartPlaying(val mediaFile: MediaFileEntity) : PlayerAction

    data class UpdatePlayerStatus(
        val duration: Duration,
        val position: Duration,
        val playing: Boolean = false,
        val trackTitle: String
    ) : PlayerAction

    class PropagateError(val errorCode: Int) : PlayerAction

    object Play : PlayerAction
    object Pause : PlayerAction
    object Stop : PlayerAction

    object ReplayOf : PlayerAction
    object ForwardOf : PlayerAction
    class SeekTo(val position: Duration) : PlayerAction
    class StopAfter(val minutes: Duration?) : PlayerAction
}
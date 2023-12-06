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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.marcocattaneo.sleep.core.utils.AbsPresenter
import dev.marcocattaneo.sleep.domain.model.MediaFileEntity
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import dev.marcocattaneo.sleep.player.presentation.player.AudioController
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class PlayerPresenter @Inject constructor(
    private val audioController: AudioController,
    private val mediaRepository: MediaRepository
) : AbsPresenter<PlayerState, PlayerEvent>() {

    @Composable
    override fun models(events: Flow<PlayerEvent>): PlayerState {
        val state = remember { mutableStateOf<PlayerState>(PlayerState.Idle) }

        LaunchedEffect(Unit) {
            events.collect { action ->
                when (action) {
                    PlayerEvent.ForwardOf -> audioController.forwardOf(30.seconds)
                    PlayerEvent.Pause -> state.updateIfIs<PlayerState.Ready> {
                        audioController.pause()
                        copy(status = PlayerState.Status.Paused)
                    }

                    PlayerEvent.Play -> state.updateIfIs<PlayerState.Ready> {
                        audioController.play()
                        copy(status = PlayerState.Status.Playing)
                    }

                    is PlayerEvent.PropagateError -> state.value =
                        PlayerState.Error(action.errorCode)

                    PlayerEvent.ReplayOf -> audioController.replayOf(30.seconds)

                    is PlayerEvent.SeekTo -> audioController.seekTo(action.position)

                    is PlayerEvent.StartPlaying -> {
                        // stop previous
                        audioController.stop()

                        state.value = mediaRepository.urlFromId(action.mediaFile.id).fold(
                            ifLeft = { PlayerState.Error(500) },
                            ifRight = {
                                // Start Audio Player with url
                                audioController.start(
                                    it,
                                    action.mediaFile.name,
                                    action.mediaFile.description
                                )

                                PlayerState.Ready(
                                    duration = 0.seconds,
                                    position = 0.seconds,
                                    stopTimer = null,
                                    trackTitle = "",
                                    status = PlayerState.Status.Playing
                                )
                            }
                        )
                    }

                    PlayerEvent.Stop -> {
                        audioController.stop()
                        state.value = PlayerState.Idle
                    }

                    is PlayerEvent.StopAfter -> {
                        state.updateIfIs<PlayerState.Ready> {
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

                    is PlayerEvent.UpdatePlayerStatus -> {
                        val currentState = state.value
                        state.value = if (currentState is PlayerState.Ready) {
                            val mustStop =
                                currentState.stopDate?.let { stopDate -> System.currentTimeMillis() > stopDate }
                                    ?: false
                            if (mustStop) {
                                audioController.stop()
                                PlayerState.Idle
                            } else {
                                currentState.copy(
                                    duration = action.duration,
                                    position = action.position,
                                    trackTitle = action.trackTitle,
                                    status = PlayerState.Status.Playing,
                                    stopDate = currentState.stopDate,
                                    stopTimer = currentState.stopTimer
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
            }
        }

        return state.value
    }

    private inline fun <reified S : PlayerState> MutableState<PlayerState>.updateIfIs(
        block: S.() -> PlayerState
    ) {
        value.let { state ->
            if (state is S) {
                value = block(state)
            }
        }
    }
}

sealed interface PlayerState {
    data object Idle : PlayerState

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

sealed interface PlayerEvent {
    data class StartPlaying(val mediaFile: MediaFileEntity) : PlayerEvent

    data class UpdatePlayerStatus(
        val duration: Duration,
        val position: Duration,
        val playing: Boolean = false,
        val trackTitle: String
    ) : PlayerEvent

    class PropagateError(val errorCode: Int) : PlayerEvent

    data object Play : PlayerEvent
    data object Pause : PlayerEvent
    data object Stop : PlayerEvent

    data object ReplayOf : PlayerEvent
    data object ForwardOf : PlayerEvent
    class SeekTo(val position: Duration) : PlayerEvent
    class StopAfter(val minutes: Duration?) : PlayerEvent
}
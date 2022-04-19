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
import arrow.core.Either
import dev.marcocattaneo.mvi.intent.*
import dev.marcocattaneo.sleep.domain.model.Minutes
import dev.marcocattaneo.sleep.domain.model.sec
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import dev.marcocattaneo.sleep.ui.player.AudioPlayer
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerIntentFactory @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val audioPlayer: AudioPlayer,
    playerStore: PlayerStore
) : IntentFactory<PlayerState, PlayerAction>(
    store = playerStore
) {
    override suspend fun buildIntent(action: PlayerAction): Intent<PlayerState> = when (action) {
        is PlayerAction.InitPlayer -> sideEffects {
            when (val result = mediaRepository.urlFromPath(action.mediaFile.path)) {
                is Either.Left -> listOf(
                    PlayerAction.UpdateStatus(PlayerState.PlayerStatus.Error(500))
                )
                is Either.Right -> listOf(
                    PlayerAction.SideEffectStartPlayer(uri = Uri.parse(result.value)),
                    PlayerAction.UpdateTrack(trackId = action.mediaFile.id)
                )
            }
        }
        is PlayerAction.UpdateDuration -> intent {
            copy(
                duration = action.duration,
                position = action.position,
                stopTimer = action.stopAfterMinutes
            )
        }
        is PlayerAction.UpdateTrack -> intent { copy(trackId = action.trackId) }
        is PlayerAction.UpdateStatus -> intent { copy(playerStatus = action.status) }
        is PlayerAction.SideEffectStartPlayer -> intent {
            audioPlayer.start(action.uri)
            this
        }
        PlayerAction.Pause -> intent {
            audioPlayer.pause()
            this
        }
        PlayerAction.Play -> intent {
            audioPlayer.play()
            this
        }
        is PlayerAction.StopAfter -> intent {
            val newTimer: Minutes? = if (this.stopTimer != action.minutes) {
                action.minutes
            } else null

            audioPlayer.stopAfter(newTimer)
            copy(stopTimer = newTimer)
        }
        PlayerAction.ForwardOf -> intent {
            audioPlayer.forwardOf(30.sec)
            this
        }
        PlayerAction.ReplayOf -> intent {
            audioPlayer.replayOf(30.sec)
            this
        }
    }.also {
        Timber.d("Built Intent for action $action")
    }

}
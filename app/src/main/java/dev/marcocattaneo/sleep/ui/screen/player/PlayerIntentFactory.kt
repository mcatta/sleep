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

import arrow.core.Either
import dagger.hilt.android.scopes.ViewModelScoped
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import dev.marcocattaneo.sleep.ui.player.AudioPlayer
import dev.marcocattaneo.mvi.intent.Intent
import dev.marcocattaneo.mvi.intent.IntentFactory
import dev.marcocattaneo.mvi.intent.intent
import dev.marcocattaneo.mvi.intent.sideEffect
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class PlayerIntentFactory @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val audioPlayer: AudioPlayer,
    playerStore: PlayerStore
) : IntentFactory<PlayerState, PlayerAction>(
    store = playerStore
) {
    override suspend fun buildIntent(action: PlayerAction): Intent<PlayerState> = when (action) {
        is PlayerAction.InitPlayer -> sideEffect {
            when (val result = mediaRepository.urlFromPath(action.urlPath)) {
                is Either.Left -> PlayerAction.UpdateStatus(PlayerState.PlayerStatus.Error(500))
                is Either.Right -> PlayerAction.SideEffectStartPlayer(result.value)
            }
        }
        is PlayerAction.UpdateDuration -> intent {
            copy(
                duration = action.duration,
                position = action.position
            )
        }
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
            audioPlayer.stopAfter(action.minutes)
            this
        }
    }.also {
        Timber.d("Built Intent for action $action")
    }

}
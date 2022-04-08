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
import dagger.hilt.android.scopes.ViewModelScoped
import dev.marcocattaneo.sleep.di.scope.CoroutineContextScope
import dev.marcocattaneo.sleep.domain.model.Path
import dev.marcocattaneo.mvi.State
import dev.marcocattaneo.mvi.intent.Action
import dev.marcocattaneo.mvi.store.ChannelStore
import dev.marcocattaneo.sleep.domain.model.Minutes
import dev.marcocattaneo.sleep.domain.model.Seconds
import dev.marcocattaneo.sleep.domain.model.sec
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ViewModelScoped
class PlayerStore @Inject constructor(
    @CoroutineContextScope scope: CoroutineScope
): ChannelStore<PlayerState>(
    scope = scope,
    initialState = PlayerState()
)

data class PlayerState(
    val duration: Seconds = 0.sec,
    val position: Seconds = 0.sec,
    val playerStatus: PlayerStatus = PlayerStatus.Stop,
    val stopTimer: Minutes? = null
) : State {
    sealed interface PlayerStatus {
        object Disposed : PlayerStatus
        object Init : PlayerStatus
        object Playing : PlayerStatus
        object Pause : PlayerStatus
        object Stop : PlayerStatus
        data class Error(val errorCode: Int) : PlayerStatus
    }
}

sealed interface PlayerAction : Action {
    data class InitPlayer(val urlPath: Path) : PlayerAction
    data class UpdateStatus(val status: PlayerState.PlayerStatus) : PlayerAction
    data class UpdateDuration(val duration: Seconds, val position: Seconds) : PlayerAction

    object Play: PlayerAction
    object Pause: PlayerAction

    object ReplayOf : PlayerAction
    object ForwardOf : PlayerAction
    data class StopAfter(val minutes: Minutes?) : PlayerAction

    data class SideEffectStartPlayer(val uri: Uri) : PlayerAction
}
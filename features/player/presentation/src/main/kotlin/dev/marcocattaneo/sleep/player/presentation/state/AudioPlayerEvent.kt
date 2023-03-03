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

package dev.marcocattaneo.sleep.player.presentation.state

import dev.marcocattaneo.sleep.domain.model.Minutes
import dev.marcocattaneo.sleep.domain.model.Seconds

sealed interface AudioPlayerEvent {
    object None : AudioPlayerEvent
    object Disposed : AudioPlayerEvent
    object Init : AudioPlayerEvent

    data class Error(val errorCode: Int) : AudioPlayerEvent
    data class PlayerStatus(
        val isPlaying: Boolean,
        val position: Seconds,
        val duration: Seconds,
        val stopAt: Minutes? = null
    ) : AudioPlayerEvent

    object Pause : AudioPlayerEvent
    object Stop : AudioPlayerEvent
}
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

package dev.marcocattaneo.sleep.player.presentation.player

import dev.marcocattaneo.sleep.player.presentation.player.state.AudioPlayerEvent
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

interface AudioPlayer {

    fun state(): StateFlow<AudioPlayerEvent>

    fun start(url: String, title: String, description: String?)

    fun pause()

    fun stop()

    fun play()

    fun seekTo(sec: Duration)

    fun dispose()

    fun forwardOf(sec: Duration)

    fun replayOf(sec: Duration)

}
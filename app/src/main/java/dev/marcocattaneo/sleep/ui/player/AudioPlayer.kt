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

package dev.marcocattaneo.sleep.ui.player

import android.media.MediaPlayer
import android.net.Uri
import dev.marcocattaneo.sleep.domain.model.Minutes
import dev.marcocattaneo.sleep.domain.model.Seconds
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {

    fun state(): StateFlow<AudioPlayerEvent>

    fun start(uri: Uri)

    fun pause()

    fun stop()

    fun play()

    fun seekTo(sec: Seconds)

    fun stopAfter(minutes: Minutes?)

    fun dispose()

    fun forwardOf(sec: Seconds)

    fun replayOf(sec: Seconds)

    val player: MediaPlayer

}
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

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import dev.marcocattaneo.sleep.di.scope.CoroutineContextScope
import dev.marcocattaneo.sleep.domain.model.Minutes
import dev.marcocattaneo.sleep.domain.model.Seconds
import dev.marcocattaneo.sleep.domain.model.sec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class AudioPlayerImpl @Inject constructor(
    @CoroutineContextScope private val coroutineScope: CoroutineScope
) : AudioPlayer {

    private val playerStateFlow = MutableStateFlow<AudioPlayerState>(AudioPlayerState.None)

    var stopDate: Long? = null

    private val timer = Timer()

    private val mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )

        setOnPreparedListener {
            start()
            updatePlayerStatus(it)
        }

        setOnErrorListener { _, what, _ ->
            emitState(AudioPlayerState.OnError(what))
            false
        }

    }

    init {
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    updatePlayerStatus(mediaPlayer)

                    stopDate?.let { stopDate ->
                        if (System.currentTimeMillis() > stopDate) {
                            stop()
                        }
                    }
                }
            }
        }, 0, 1_000)
    }

    private fun updatePlayerStatus(mediaPlayer: MediaPlayer) = emitState(
        AudioPlayerState.PlayerStatus(
            isPlaying = mediaPlayer.isPlaying,
            duration = mediaPlayer.duration.div(1_000L).sec,
            position = mediaPlayer.currentPosition.div(1_000L).sec
        )
    )

    override fun state(): StateFlow<AudioPlayerState> = playerStateFlow

    private fun emitState(audioPlayerState: AudioPlayerState) = coroutineScope.launch {
        playerStateFlow.emit(audioPlayerState)
    }

    override fun start(uri: Uri) {
        stopDate = null
        emitState(AudioPlayerState.OnInit)
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.apply {
            setDataSource(uri.toString())
            prepareAsync()
        }
    }

    override fun pause() = mediaPlayer.pause().also {
        emitState(AudioPlayerState.OnPause)
    }

    override fun play() = mediaPlayer.start().also {
        updatePlayerStatus(mediaPlayer)
    }

    override fun stop() = mediaPlayer.stop().also {
        emitState(AudioPlayerState.OnStop)
    }

    override fun stopAfter(minutes: Minutes?) {
        stopDate = minutes?.let { System.currentTimeMillis().plus(it.value.times(60).times(1_000)) }
    }

    override fun dispose() {
        mediaPlayer.release()
        timer.cancel()
        emitState(AudioPlayerState.Disposed)
    }

    override fun forwardOf(sec: Seconds) {
        min(player.duration, player.currentPosition + (sec.value.toInt().times(1_000)))
            .let(mediaPlayer::seekTo)
    }

    override fun replayOf(sec: Seconds) {
        max(0, player.currentPosition - (sec.value.toInt()).times(1_000))
            .let(mediaPlayer::seekTo)
    }

    override val player: MediaPlayer
        get() = mediaPlayer

}

sealed interface AudioPlayerState {
    object None : AudioPlayerState
    object Disposed : AudioPlayerState
    object OnInit : AudioPlayerState

    data class OnError(val errorCode: Int) : AudioPlayerState
    data class PlayerStatus(
        val isPlaying: Boolean,
        val position: Seconds,
        val duration: Seconds
    ) : AudioPlayerState

    object OnPause : AudioPlayerState
    object OnStop : AudioPlayerState
}
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

package dev.marcocattaneo.sleep.player.presentation

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.marcocattaneo.sleep.core.di.scope.CoroutineContextScope
import dev.marcocattaneo.sleep.player.presentation.session.SessionManager
import dev.marcocattaneo.sleep.player.presentation.state.AudioPlayerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


class AudioPlayerImpl @Inject constructor(
    @CoroutineContextScope private val coroutineScope: CoroutineScope,
    private val sessionManager: SessionManager,
    private val mediaPlayer: MediaPlayer
) : AudioPlayer {

    init {
        sessionManager.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                sessionManager.isActive = true
                sessionManager.setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
                play()
            }

            override fun onPause() {
                super.onPause()
                sessionManager.setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
                pause()
            }

        })
    }

    private val playerStateFlow = MutableStateFlow<AudioPlayerEvent>(AudioPlayerEvent.None)

    private var stopDate: Long? = null

    private var stopAfterMinutes: Duration? = null

    private val timer = Timer()

    init {
        mediaPlayer.apply {
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
                .let(::setAudioAttributes)

            setOnPreparedListener {
                start()
                updatePlayerStatus(it)
            }

            setOnErrorListener { _, what, _ ->
                emitState(AudioPlayerEvent.Error(what))
                false
            }

            setOnCompletionListener {
                emitState(AudioPlayerEvent.Stop)
            }

        }

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

    private fun updatePlayerStatus(mediaPlayer: MediaPlayer) = updatePlayerStatusWithPosition(
        mediaPlayer = mediaPlayer,
        position = mediaPlayer.currentPosition.div(1_000L).seconds
    )

    private fun updatePlayerStatusWithPosition(mediaPlayer: MediaPlayer, position: Duration) = emitState(
        AudioPlayerEvent.PlayerStatus(
            isPlaying = mediaPlayer.isPlaying,
            duration = mediaPlayer.duration.div(1_000L).seconds,
            position = position,
            stopAt = stopAfterMinutes
        )
    )

    override fun state(): StateFlow<AudioPlayerEvent> = playerStateFlow

    private fun emitState(audioPlayerEvent: AudioPlayerEvent) = coroutineScope.launch {
        playerStateFlow.emit(audioPlayerEvent)
    }

    override fun start(url: String, title: String, description: String?) {
        sessionManager.initMetaData(title, description)
        stopDate = null
        stopAfterMinutes = null
        emitState(AudioPlayerEvent.Init)
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.apply {
            setDataSource(url)
            prepareAsync()
        }
    }

    override fun pause() = mediaPlayer.pause().also {
        emitState(AudioPlayerEvent.Pause)
    }

    override fun play() = mediaPlayer.start().also() {
        updatePlayerStatus(mediaPlayer)
    }

    override fun seekTo(sec: Duration) = sec.inWholeMilliseconds
        .toInt()
        .let(mediaPlayer::seekTo)
        .also {
            updatePlayerStatusWithPosition(
                mediaPlayer = mediaPlayer,
                position = sec
            )
        }

    override fun stop() = mediaPlayer.stop().also {
        emitState(AudioPlayerEvent.Stop)
    }

    override fun stopAfter(minutes: Duration?) {
        stopAfterMinutes = minutes
        stopDate = minutes?.let { System.currentTimeMillis().plus(it.inWholeMilliseconds) }
    }

    override fun dispose() {
        mediaPlayer.release()
        timer.cancel()
        emitState(AudioPlayerEvent.Disposed)
    }

    override fun forwardOf(sec: Duration) {
        min(player.duration.milliseconds.inWholeMilliseconds, player.currentPosition + (sec.inWholeMilliseconds))
            .toInt()
            .let(mediaPlayer::seekTo)
    }

    override fun replayOf(sec: Duration) {
        max(0, player.currentPosition - sec.inWholeMilliseconds)
            .toInt()
            .let(mediaPlayer::seekTo)
    }

    override val player: MediaPlayer
        get() = mediaPlayer

    override val sessionToken: MediaSessionCompat.Token
        get() = sessionManager.sessionToken

    override val controller: MediaControllerCompat
        get() = sessionManager.controller

}


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

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.marcocattaneo.sleep.core.di.scope.CoroutineMainScope
import dev.marcocattaneo.sleep.player.presentation.player.state.AudioPlayerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class AudioControllerImpl @Inject constructor(
    @CoroutineMainScope private val coroutineScope: CoroutineScope,
    @ApplicationContext private val context: Context
) : AudioController {

    private companion object {
        const val UPDATE_PERIOD = 1_000L
    }

    private var mediaController: MediaController? = null
    private val playerStateFlow = MutableStateFlow<AudioPlayerEvent>(AudioPlayerEvent.None)
    private val timer = Timer()

    private val playerListener: Player.Listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)

            println(events)
        }
    }

    init {
        timer.schedule(object : TimerTask() {
            override fun run() {
                onController {
                    coroutineScope.launch {
                        if (isPlaying) {
                            updatePlayerStatus(this@onController)
                        }
                    }
                }
            }
        }, 0, UPDATE_PERIOD)
    }

    override fun state(): StateFlow<AudioPlayerEvent> {
        return playerStateFlow.asStateFlow()
    }

    /**
     * Emi AudioEvent state
     *
     * @param audioPlayerEvent
     */
    private fun emitState(audioPlayerEvent: AudioPlayerEvent) = coroutineScope.launch {
        playerStateFlow.emit(audioPlayerEvent)
    }

    /**
     * Prepare and play a track with url [url]
     *
     * @param url track's URL
     * @param title track's title
     * @param description track's description
     */
    private fun MediaController.prepareAndPlay(
        url: String,
        title: String,
        description: String?
    ) {
        val mediaItem = MediaItem.Builder()
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setDescription(description)
                    .build()
            )
            .setUri(url)
            .build()
        setMediaItem(mediaItem)
        prepare()
        play()
        updatePlayerStatus(this)
    }

    override fun start(url: String, title: String, description: String?) {
        if (mediaController == null) {
            val sessionToken =
                SessionToken(context, ComponentName(context, PlaybackService::class.java))

            val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture.addListener(
                {
                    mediaController = controllerFuture.get()
                    mediaController?.prepareAndPlay(url, title, description)
                    mediaController!!.addListener(playerListener)
                },
                MoreExecutors.directExecutor()
            )
        } else {
            mediaController?.prepareAndPlay(url, title, description)
        }
    }

    override fun pause() = onController {
        pause()
    }

    override fun stop() = onController {
        stop()
    }

    override fun play() = onController {
        play()
        updatePlayerStatus(this)
    }

    override fun seekTo(sec: Duration) = onController {
        seekTo(sec.inWholeMilliseconds)
        updatePlayerStatusWithPosition(
            controller = this,
            position = sec
        )
    }

    override fun dispose() = onController {
        release()
        timer.cancel()
        emitState(AudioPlayerEvent.Disposed)
    }

    override fun forwardOf(sec: Duration) = onController {
        seekTo(
            min(
                duration.milliseconds.inWholeMilliseconds,
                currentPosition + (sec.inWholeMilliseconds)
            )
        )
    }

    override fun replayOf(sec: Duration) = onController {
        seekTo(max(0, currentPosition - sec.inWholeMilliseconds))
    }

    private fun updatePlayerStatus(controller: MediaController) = updatePlayerStatusWithPosition(
        controller = controller,
        position = controller.currentPosition.div(1_000L).seconds
    )

    private fun updatePlayerStatusWithPosition(
        controller: MediaController,
        position: Duration
    ) {
        emitState(
            AudioPlayerEvent.PlayerStatus(
                isPlaying = controller.isPlaying,
                duration = controller.duration.div(1_000L).seconds,
                position = position,
                trackTitle = ""
            )
        )
    }


    private fun onController(block: MediaController.() -> Unit) {
        mediaController?.apply(block)
    }

}
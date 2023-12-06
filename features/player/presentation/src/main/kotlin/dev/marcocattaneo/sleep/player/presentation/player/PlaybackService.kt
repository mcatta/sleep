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

import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    private fun createPlayer() = ExoPlayer.Builder(baseContext)
        .build()
        .apply {
            val attr = AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build()
            setAudioAttributes(attr, true)

        }

    private val callback = SessionCallback()

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = let {
            val player = createPlayer()
            MediaSession.Builder(this, player)
                .setCallback(callback)
                .build()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player ?: return

        if (!player.playWhenReady || player.mediaItemCount == 0) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            stopSelf()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    private inner class SessionCallback : MediaSession.Callback {

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ) : MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)
            val sessionCommands = connectionResult.availableSessionCommands
                    .buildUpon()
                    .build()
            return MediaSession.ConnectionResult.accept(
                sessionCommands, connectionResult.availablePlayerCommands
            )
        }

        override fun onPostConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ) {
            session.setCustomLayout(listOf())

            super.onPostConnect(session, controller)
        }

    }
}
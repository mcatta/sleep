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

package dev.marcocattaneo.sleep.ui.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import dev.marcocattaneo.sleep.player.presentation.AudioPlayer
import dev.marcocattaneo.sleep.player.presentation.state.AudioPlayerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlayerNotificationService : Service() {

    sealed class Action(val key: String) {
        data object PLAY : Action("dev.marcocattaneo.sleep.PlayerNotificationService.PLAY")

        data object PAUSE : Action("dev.marcocattaneo.sleep.PlayerNotificationService.PAUSE")

        data object STOP : Action("dev.marcocattaneo.sleep.PlayerNotificationService.STOP")
    }

    @Inject
    lateinit var audioPlayer: AudioPlayer

    @Inject
    lateinit var playerNotificationManager: PlayerNotificationManager

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate() {
        super.onCreate()

        startForegroundService()

        scope.launch {
            audioPlayer.state().collect {
                when (it) {
                    AudioPlayerEvent.Init -> startForegroundService()
                    is AudioPlayerEvent.PlayerStatus -> if (it.isPlaying) playerNotificationManager.updateNotification(
                        isPlaying = true
                    )

                    is AudioPlayerEvent.Error -> Unit
                    is AudioPlayerEvent.Disposed -> stopForegroundService()

                    is AudioPlayerEvent.Stop,
                    is AudioPlayerEvent.None -> Unit
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.PAUSE.key -> audioPlayer.pause()
            Action.PLAY.key -> audioPlayer.play()
            Action.STOP.key -> audioPlayer.stop()
        }

        return START_STICKY
    }

    private fun startForegroundService() {
        startForeground(PlayerNotificationManager.NOTIFICATION_ID, playerNotificationManager.foregroundNotification())
    }

    private fun stopForegroundService() {
        playerNotificationManager.removeNotification()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}
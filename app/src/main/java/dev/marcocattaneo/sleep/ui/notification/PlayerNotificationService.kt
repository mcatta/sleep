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
import dev.marcocattaneo.sleep.ui.player.AudioPlayer
import dev.marcocattaneo.sleep.ui.player.AudioPlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlayerNotificationService: Service()  {


    companion object {
        const val ACTION_PLAY = "dev.marcocattaneo.sleep.PlayerNotificationService.PLAY"
        const val ACTION_PAUSE = "dev.marcocattaneo.sleep.PlayerNotificationService.PAUSE"
    }

    @Inject
    lateinit var audioPlayer: AudioPlayer

    @Inject
    lateinit var playerNotificationManager: PlayerNotificationManager

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate() {
        super.onCreate()

        scope.launch {
            audioPlayer.state().collect {
                when (it) {
                    is AudioPlayerState.PlayerStatus -> playerNotificationManager.updateNotification(
                        isPlaying = it.isPlaying
                    )
                    is AudioPlayerState.OnPause -> playerNotificationManager.updateNotification(
                        isPlaying = false
                    )

                    is AudioPlayerState.OnError,
                    AudioPlayerState.OnStop,
                    AudioPlayerState.Disposed -> playerNotificationManager.removeNotification()

                    AudioPlayerState.OnInit,
                    AudioPlayerState.None -> Unit
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_PAUSE -> audioPlayer.pause()
            ACTION_PLAY -> audioPlayer.play()
        }

        startForeground(PlayerNotificationManager.NOTIFICATION_ID, playerNotificationManager.foregroundNotification())

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.dispose()
        job.cancel()
    }

}
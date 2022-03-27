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
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PlayerNotificationService: Service()  {

    @Inject
    lateinit var audioPlayer: AudioPlayer

    @Inject
    lateinit var playerNotificationManager: PlayerNotificationManager

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        scope.launch {
            audioPlayer.state().collect {
                Timber.d("AudioPlayer state $it")

                when (it) {
                    AudioPlayerState.OnInit -> playerNotificationManager.createNotification()
                    is AudioPlayerState.PlayerStatus -> playerNotificationManager.updateNotification(
                        position = it.position,
                        duration = it.duration,
                        isPlaying = it.isPlaying
                    )
                    AudioPlayerState.Disposed -> playerNotificationManager.removeNotification()
                    else -> {
                        // Nop
                    }
                }
            }
        }
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.dispose()
        job.cancel()
    }

}
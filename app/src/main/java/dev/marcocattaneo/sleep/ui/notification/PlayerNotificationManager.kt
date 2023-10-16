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

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.marcocattaneo.sleep.player.presentation.R as PlayerR
import dev.marcocattaneo.sleep.R as AppR
import dev.marcocattaneo.sleep.player.presentation.AudioPlayer
import javax.inject.Inject

class PlayerNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioPlayer: AudioPlayer
) {

    companion object {
        const val CHANNEL_ID = "Player_Notification_Channel_ID"

        const val NOTIFICATION_ID = 93
    }

    private val pausePendingIntent: PendingIntent
        get() = createPendingIntent(PlayerNotificationService.Action.PAUSE)

    private val playPendingIntent: PendingIntent
        get() = createPendingIntent(PlayerNotificationService.Action.PLAY)

    private val stopPendingIntent: PendingIntent
        get() = createPendingIntent(PlayerNotificationService.Action.STOP)

    private fun createPendingIntent(action: PlayerNotificationService.Action) =
        PendingIntent.getService(
            context,
            0,
            Intent(context, PlayerNotificationService::class.java).setAction(action.key),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(AppR.string.app_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Audio Player"
                setSound(null, null)
            }
            // Register the channel with the system
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun NotificationCompat.Builder.show() = if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        throw IllegalStateException("You need to add the permission ${Manifest.permission.POST_NOTIFICATIONS} to your AndroidManifest.xml")
    } else NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, build())

    private fun baseNotification(
        cancelable: Boolean
    ) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setContentTitle(context.getString(AppR.string.app_name))
        .setContentIntent(audioPlayer.controller.sessionActivity)
        .setDeleteIntent(createPendingIntent(PlayerNotificationService.Action.STOP))
        .setSmallIcon(AppR.mipmap.ic_launcher)
        .setAutoCancel(cancelable)


    fun updateNotification(
        isPlaying: Boolean,
    ) = baseNotification(cancelable = !isPlaying)
        .apply {
            addAction(
                NotificationCompat.Action.Builder(
                    PlayerR.drawable.ic_baseline_close_24,
                    "Stop",
                    stopPendingIntent
                ).build()
            )
            if (isPlaying) {
                addAction(
                    NotificationCompat.Action.Builder(
                        PlayerR.drawable.ic_baseline_pause_24,
                        "Pause",
                        pausePendingIntent
                    ).build()
                )
            } else {
                addAction(
                    NotificationCompat.Action.Builder(
                        PlayerR.drawable.ic_baseline_play_arrow_24,
                        "Play",
                        playPendingIntent
                    ).build()
                )
            }
        }
        .setStyle(
            MediaStyle().setShowActionsInCompactView(1).setMediaSession(audioPlayer.sessionToken)
        )
        .show()

    fun removeNotification() = NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)

    fun foregroundNotification(): Notification = baseNotification(cancelable = true)
        .build()

}
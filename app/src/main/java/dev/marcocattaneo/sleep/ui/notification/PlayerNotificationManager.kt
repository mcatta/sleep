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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.marcocattaneo.sleep.R
import javax.inject.Inject

class PlayerNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CHANNEL_ID = "Player_Notification_Channel_ID"

        const val NOTIFICATION_ID = 93
    }

    private val pausePendingIntent: PendingIntent
        get() = createPendingIntent(PlayerNotificationService.ACTION_PAUSE)

    private val playPendingIntent: PendingIntent
        get() = createPendingIntent(PlayerNotificationService.ACTION_PLAY)

    private fun createPendingIntent(action: String) = PendingIntent.getService(
        context,
        0,
        Intent(context, PlayerNotificationService::class.java).setAction(action),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
    )

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.app_name),
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

    private fun NotificationCompat.Builder.show() =
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, build())

    private fun baseNotification() = NotificationCompat.Builder(context, CHANNEL_ID)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setAutoCancel(false)

    fun updateNotification(
        isPlaying: Boolean
    ) = baseNotification()
        .apply {
            if (isPlaying) {
                addAction(R.drawable.ic_baseline_pause_24, "Pause", pausePendingIntent)
            } else {
                addAction(R.drawable.ic_baseline_play_arrow_24, "Play", playPendingIntent)
            }
        }
        .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0))
        .show()

    fun removeNotification() = NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)

    fun foregroundNotification(): Notification = baseNotification().build()

}
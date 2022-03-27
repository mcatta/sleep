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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Audio Player"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification() = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("My notification")
        .setContentText("Hello World!")
        // Set the intent that will fire when the user taps the notification
        //.setContentIntent(pendingIntent)
        .setAutoCancel(false)
        .show()

    fun updateNotification(
        position: Int,
        duration: Int,
        isPlaying: Boolean
    ) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("My notification")
        .setContentText("isPlaying $isPlaying")
        // Set the intent that will fire when the user taps the notification
        //.setContentIntent(pendingIntent)
        .setAutoCancel(false)
        .show()

    private fun NotificationCompat.Builder.show() {
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, build())
    }

    fun removeNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

}
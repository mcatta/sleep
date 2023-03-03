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

package dev.marcocattaneo.sleep.player.presentation.session

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.marcocattaneo.sleep.player.presentation.R
import javax.inject.Inject

class SessionManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SessionManager {

    private val stateBuilder: PlaybackStateCompat.Builder

    // Create a MediaSessionCompat
    private val mediaSession = MediaSessionCompat(context, "MediaPlaybackService_TAG").apply {

        // Enable callbacks from MediaButtons and TransportControls
        setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        stateBuilder = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)
        setPlaybackState(stateBuilder.build())
    }

    override fun setCallback(callback: MediaSessionCompat.Callback) = mediaSession.setCallback(callback)

    override fun initMetaData(title: String, description: String?) {
        val metadataBuilder = MediaMetadataCompat.Builder()
        //Notification icon in card
        metadataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        )
        metadataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        )

        //lock screen icon for pre lollipop
        metadataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_ART,
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        )
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
        description?.let {
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, it)
        }
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1)
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 1)

        mediaSession.setMetadata(metadataBuilder.build())
    }

    override fun setMediaPlaybackState(state: Int) {
        val playbackStateBuilder = PlaybackStateCompat.Builder()
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE)
        } else {
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY)
        }
        playbackStateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        mediaSession.setPlaybackState(playbackStateBuilder.build())
    }

    override var isActive: Boolean
        get() = mediaSession.isActive
        set(value) {
            mediaSession.isActive = value
        }

    override val sessionToken: MediaSessionCompat.Token
        get() = mediaSession.sessionToken

    override val controller: MediaControllerCompat
        get() = mediaSession.controller

}
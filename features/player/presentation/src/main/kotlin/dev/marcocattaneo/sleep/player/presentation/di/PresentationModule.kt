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

package dev.marcocattaneo.sleep.player.presentation.di

import android.content.Context
import android.media.MediaPlayer
import androidx.media3.exoplayer.ExoPlayer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.marcocattaneo.sleep.player.presentation.player.AudioPlayer
import dev.marcocattaneo.sleep.player.presentation.player.AudioPlayerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PresentationModule {

    @Binds
    @Singleton
    abstract fun provideAudioPlayer(
        player: AudioPlayerImpl
    ): AudioPlayer

}

@Module
@InstallIn(SingletonComponent::class)
class PresentationProviderModule {

    @Provides
    @Singleton
    fun provideMediaPlayer(): MediaPlayer = MediaPlayer()

    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context
    ): ExoPlayer = ExoPlayer.Builder(context).build()

}
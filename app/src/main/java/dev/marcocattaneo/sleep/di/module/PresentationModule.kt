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

package dev.marcocattaneo.sleep.di.module

import android.media.MediaPlayer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.marcocattaneo.sleep.player.presentation.AudioPlayer
import dev.marcocattaneo.sleep.player.presentation.AudioPlayerImpl
import dev.marcocattaneo.sleep.player.presentation.session.SessionManager
import dev.marcocattaneo.sleep.player.presentation.session.SessionManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PresentationModule {

    @Binds
    @Singleton
    abstract fun provideAudioPlayer(
        player: AudioPlayerImpl
    ): AudioPlayer

    @Binds
    @Singleton
    abstract fun provideSessionManager(
        sessionManager: SessionManagerImpl
    ): SessionManager

}

@Module
@InstallIn(SingletonComponent::class)
class PresentationProviderModule {

    @Provides
    @Singleton
    fun provideMediaPlayer(): MediaPlayer = MediaPlayer()

}
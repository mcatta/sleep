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

package dev.marcocattaneo.sleep.player.presentation

import android.media.MediaPlayer
import app.cash.turbine.test
import dev.marcocattaneo.core.testing.CoroutinesTestRule
import dev.marcocattaneo.sleep.domain.model.sec
import dev.marcocattaneo.sleep.player.presentation.session.SessionManager
import dev.marcocattaneo.sleep.player.presentation.state.AudioPlayerEvent
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertIs


@OptIn(ExperimentalCoroutinesApi::class)
@Ignore("We need RobolectricTestRunner but it's not compatible for the target 33")
internal class AudioPlayerImplTest {

    private lateinit var mediaPlayer: MediaPlayer

    @RelaxedMockK
    lateinit var sessionManager: SessionManager

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)
        mediaPlayer = mockk(relaxed = true)
    }

    private fun getImpl(): AudioPlayer = AudioPlayerImpl(coroutinesTestRule.scope, sessionManager, mediaPlayer)

    @Test
    fun `Test init`() {
        // When
        getImpl()

        // Then
        verify { mediaPlayer.setAudioAttributes(any()) }
        verify { sessionManager.setCallback(any()) }
    }

    @Test
    fun `Test start`() = coroutinesTestRule.scope.runTest {
        // Given
        val audioPlayer = getImpl()
        every { mediaPlayer.setDataSource(any<String>()) } just Runs
        every { mediaPlayer.prepareAsync() } just Runs

        audioPlayer.state().test {
            // When
            audioPlayer.start("https://my-url", "title", "description")

            // Then
            verify { mediaPlayer.stop() }
            verify { mediaPlayer.reset() }
            verify { mediaPlayer.prepareAsync() }
            verify { mediaPlayer.setDataSource(eq("https://my-url")) }

            assertIs<AudioPlayerEvent.None>(awaitItem())
            assertIs<AudioPlayerEvent.Init>(awaitItem())

            verify { sessionManager.initMetaData(eq("title"), eq("description")) }
        }
    }

    @Test
    fun `Test stop`() = coroutinesTestRule.scope.runTest {
        // Given
        val audioPlayer = getImpl()

        audioPlayer.state().test {
            // When
            audioPlayer.stop()

            // Then
            verify { mediaPlayer.stop() }

            assertIs<AudioPlayerEvent.None>(awaitItem())
            assertIs<AudioPlayerEvent.Stop>(awaitItem())
        }
    }

    @Test
    fun `Test pause`() = coroutinesTestRule.scope.runTest {
        // Given
        val audioPlayer = getImpl()

        audioPlayer.state().test {
            // When
            audioPlayer.pause()

            // Then
            verify { mediaPlayer.pause() }

            assertIs<AudioPlayerEvent.None>(awaitItem())
            assertIs<AudioPlayerEvent.Pause>(awaitItem())
        }
    }

    @Test
    fun `Test dispose`() = coroutinesTestRule.scope.runTest {
        // Given
        val audioPlayer = getImpl()

        audioPlayer.state().test {
            // When
            audioPlayer.dispose()

            // Then
            verify { mediaPlayer.release() }

            assertIs<AudioPlayerEvent.None>(awaitItem())
            assertIs<AudioPlayerEvent.Disposed>(awaitItem())
        }
    }

    @Test
    fun `Test seekTo`() = coroutinesTestRule.scope.runTest {
        // Given
        val audioPlayer = getImpl()
        every { mediaPlayer.duration } returns 0

        audioPlayer.state().test {
            // When
            audioPlayer.seekTo(30.sec)

            // Then
            verify { mediaPlayer.seekTo(30_000) }

            assertIs<AudioPlayerEvent.None>(awaitItem())
            assertIs<AudioPlayerEvent.PlayerStatus>(awaitItem())
        }
    }

    @Test
    fun `Test forward`() = coroutinesTestRule.scope.runTest {
        // Given
        val audioPlayer = getImpl()
        every { mediaPlayer.currentPosition } returns 20_000
        every { mediaPlayer.duration } returns 120_000

        audioPlayer.state().test {
            // When
            audioPlayer.forwardOf(30.sec)

            // Then
            verify { mediaPlayer.seekTo(50_000) }

            assertIs<AudioPlayerEvent.None>(awaitItem())
        }
    }

    @Test
    fun `Test replayOf`() = coroutinesTestRule.scope.runTest {
        // Given
        val audioPlayer = getImpl()
        every { mediaPlayer.currentPosition } returns 30_000

        audioPlayer.state().test {
            // When
            audioPlayer.replayOf(30.sec)

            // Then
            verify { mediaPlayer.seekTo(0) }

            assertIs<AudioPlayerEvent.None>(awaitItem())
        }
    }

}



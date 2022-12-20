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

package dev.marcocattaneo.sleep.ui.screen.player

import app.cash.turbine.test
import arrow.core.Either
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.model.sec
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import dev.marcocattaneo.sleep.fakeMediaFile
import dev.marcocattaneo.sleep.ui.player.AudioPlayer
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
internal class PlayerStateMachineTest {

    @RelaxedMockK
    lateinit var audioPlayer: AudioPlayer

    @RelaxedMockK
    lateinit var playlistStateMachine: PlaylistStateMachine

    @RelaxedMockK
    lateinit var mediaRepository: MediaRepository

    private lateinit var playerStateMachine: PlayerStateMachine

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)
        playerStateMachine = PlayerStateMachine(
            audioPlayer = audioPlayer,
            playlistStateMachine = playlistStateMachine,
            mediaRepository = mediaRepository
        )
    }

    @Test
    fun `Test PlayerAction StartPlaying`() = runTest {
        // Given
        coEvery { mediaRepository.urlFromPath(any()) } returns Either.Right("https://resource")

        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(PlayerAction.StartPlaying(fakeMediaFile()))

            // Then
            assertIs<PlayerState>(awaitItem())
            assertIs<PlayerState.Init>(awaitItem())

            coVerify { audioPlayer.stop() }
            coVerify { mediaRepository.urlFromPath(any()) }
            coVerify { audioPlayer.start(any(), any(), any()) }
            coVerify { playlistStateMachine.dispatch(ofType<PlaylistAction.Update>()) }
        }
    }

    @Test
    fun `Test PlayerAction StartPlaying upon failure`() = runTest {
        // Given
        coEvery { mediaRepository.urlFromPath(any()) } returns Either.Left(AppException.FileNotFound)

        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(PlayerAction.StartPlaying(fakeMediaFile()))

            // Then
            assertIs<PlayerState>(awaitItem())
            assertIs<PlayerState.Error>(awaitItem())
            assertIs<PlayerState.Stop>(awaitItem())

            coVerify { audioPlayer.stop() }
            coVerify { mediaRepository.urlFromPath(any()) }
            coVerify(exactly = 0) { audioPlayer.start(any(), any(), any()) }
            coVerify(exactly = 0) { playlistStateMachine.dispatch(ofType<PlaylistAction.Update>()) }
        }
    }

    @Test
    fun `Test PlayerAction Stop`() = runTest {
        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(PlayerAction.Stop)

            // Then
            assertIs<PlayerState.Stop>(awaitItem())
            coVerify { audioPlayer.stop() }
        }
    }

    @Test
    fun `Test PlayerAction Pause`() = runTest {
        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(
                PlayerAction.UpdateDuration(
                    duration = 100.sec,
                    position = 20.sec,
                    stopAfterMinutes = null,
                    playing = true
                )
            )
            playerStateMachine.dispatch(PlayerAction.Pause)

            // Then
            assertIs<PlayerState.Stop>(awaitItem())
            assertIs<PlayerState.Playing>(awaitItem())
            assertIs<PlayerState.Pause>(awaitItem())
            coVerify { audioPlayer.pause() }
        }
    }

    @Test
    fun `Test PlayerAction Pause and Play`() = runTest {
        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(
                PlayerAction.UpdateDuration(
                    duration = 100.sec,
                    position = 20.sec,
                    stopAfterMinutes = null,
                    playing = true
                )
            )
            playerStateMachine.dispatch(PlayerAction.Pause)
            playerStateMachine.dispatch(PlayerAction.Play)

            // Then
            assertIs<PlayerState.Stop>(awaitItem())
            assertIs<PlayerState.Playing>(awaitItem())
            assertIs<PlayerState.Pause>(awaitItem())
            assertIs<PlayerState.Playing>(awaitItem())
            coVerify { audioPlayer.pause() }
            coVerify { audioPlayer.play() }
        }
    }

    @Test
    fun `Test PlayerAction UpdateDuration`() = runTest {
        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(
                PlayerAction.UpdateDuration(
                    duration = 100.sec,
                    position = 20.sec,
                    stopAfterMinutes = null,
                    playing = true
                )
            )

            // Then
            assertIs<PlayerState.Stop>(awaitItem())
            awaitItem().let {
                assertIs<PlayerState.Playing>(it)

                assertEquals(100L, it.duration.value)
                assertEquals(20L, it.position.value)
                assertNull(it.stopTimer)
            }
        }
    }

    @Test
    fun `Test PlayerAction Seeking`() = runTest {
        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(
                PlayerAction.UpdateDuration(
                    duration = 100.sec,
                    position = 20.sec,
                    stopAfterMinutes = null,
                    playing = true
                )
            )
            playerStateMachine.dispatch(PlayerAction.SeekTo(42.sec))

            // Then
            assertIs<PlayerState.Stop>(awaitItem())
            assertIs<PlayerState.Playing>(awaitItem())
            coVerify { audioPlayer.seekTo(any()) }
        }
    }

    @Test
    fun `Test PlayerAction ForwardOf`() = runTest {
        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(
                PlayerAction.UpdateDuration(
                    duration = 100.sec,
                    position = 20.sec,
                    stopAfterMinutes = null,
                    playing = true
                )
            )
            playerStateMachine.dispatch(PlayerAction.ForwardOf)

            // Then
            assertIs<PlayerState.Stop>(awaitItem())
            assertIs<PlayerState.Playing>(awaitItem())
            coVerify { audioPlayer.forwardOf(any()) }
        }
    }

    @Test
    fun `Test PlayerAction ReplayOf`() = runTest {
        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(
                PlayerAction.UpdateDuration(
                    duration = 100.sec,
                    position = 20.sec,
                    stopAfterMinutes = null,
                    playing = true
                )
            )
            playerStateMachine.dispatch(PlayerAction.ReplayOf)

            // Then
            assertIs<PlayerState.Stop>(awaitItem())
            assertIs<PlayerState.Playing>(awaitItem())
            coVerify { audioPlayer.replayOf(any()) }
        }
    }

}
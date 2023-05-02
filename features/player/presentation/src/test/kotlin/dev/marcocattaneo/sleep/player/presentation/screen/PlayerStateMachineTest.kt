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

package dev.marcocattaneo.sleep.player.presentation.screen

import app.cash.turbine.test
import arrow.core.Either
import dev.marcocattaneo.core.testing.anyValue
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import dev.marcocattaneo.sleep.player.presentation.AudioPlayer
import dev.marcocattaneo.sleep.player.presentation.fakeMediaFile
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
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
        coEvery { mediaRepository.urlFromId(any()) } returns Either.Right("https://resource")

        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(PlayerAction.StartPlaying(fakeMediaFile()))

            // Then
            assertIs<PlayerState>(awaitItem())
            assertIs<PlayerState.Init>(awaitItem())

            coVerify { audioPlayer.stop() }
            coVerify { mediaRepository.urlFromId(any()) }
            coVerify { audioPlayer.start(any(), any(), any()) }
            coVerify { playlistStateMachine.dispatch(ofType<PlaylistAction.Update>()) }
        }
    }

    @Test
    fun `Test PlayerAction StartPlaying upon failure`() = runTest {
        // Given
        coEvery { mediaRepository.urlFromId(any()) } returns Either.Left(AppException.FileNotFound)

        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(PlayerAction.StartPlaying(fakeMediaFile()))

            // Then
            assertIs<PlayerState>(awaitItem())
            assertIs<PlayerState.Error>(awaitItem())
            assertIs<PlayerState.Stop>(awaitItem())

            coVerify { audioPlayer.stop() }
            coVerify { mediaRepository.urlFromId(any()) }
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
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    stopAfterMinutes = null,
                    playing = true,
                    trackTitle = "title"
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
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    stopAfterMinutes = null,
                    playing = true,
                    trackTitle = "title"
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
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    stopAfterMinutes = null,
                    playing = true,
                    trackTitle = "title"
                )
            )

            // Then
            assertIs<PlayerState.Stop>(awaitItem())
            awaitItem().let {
                assertIs<PlayerState.Playing>(it)

                assertEquals("title", it.trackTitle)
                assertEquals(100L, it.duration.inWholeSeconds)
                assertEquals(20L, it.position.inWholeSeconds)
                assertNull(it.stopTimer)
            }
        }
    }

    @Test
    fun `Test PlayerAction Seeking`() = runTest {
        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    stopAfterMinutes = null,
                    playing = true,
                    trackTitle = "title"
                )
            )
            playerStateMachine.dispatch(PlayerAction.SeekTo(42.seconds))

            // Then
            assertIs<PlayerState.Stop>(awaitItem())
            assertIs<PlayerState.Playing>(awaitItem())
            verify { audioPlayer.seekTo(anyValue()) }
        }
    }

    @Test
    fun `Test PlayerAction ForwardOf`() = runTest {
        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    stopAfterMinutes = null,
                    playing = true,
                    trackTitle = "title"
                )
            )
            playerStateMachine.dispatch(PlayerAction.ForwardOf)

            // Then
            assertIs<PlayerState.Stop>(awaitItem())
            assertIs<PlayerState.Playing>(awaitItem())
            verify { audioPlayer.forwardOf(anyValue()) }
        }
    }

    @Test
    fun `Test PlayerAction ReplayOf`() = runTest {
        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    stopAfterMinutes = null,
                    playing = true,
                    trackTitle = "title"
                )
            )
            playerStateMachine.dispatch(PlayerAction.ReplayOf)

            // Then
            assertIs<PlayerState.Stop>(awaitItem())
            assertIs<PlayerState.Playing>(awaitItem())
            coVerify { audioPlayer.replayOf(anyValue()) }
        }
    }

    @Test
    fun `Test PlayerAction PropagateError`() = runTest {
        playerStateMachine.state.test {
            // When
            playerStateMachine.dispatch(
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    stopAfterMinutes = null,
                    playing = true,
                    trackTitle = "title"
                )
            )
            playerStateMachine.dispatch(PlayerAction.PropagateError(500))

            // Then
            assertIs<PlayerState.Stop>(awaitItem())
            assertIs<PlayerState.Playing>(awaitItem())
            assertIs<PlayerState.Error>(awaitItem())
            assertIs<PlayerState.Stop>(awaitItem())
        }
    }

}
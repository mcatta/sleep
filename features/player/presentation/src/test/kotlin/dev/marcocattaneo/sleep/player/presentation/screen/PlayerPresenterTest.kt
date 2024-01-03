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

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import arrow.core.Either
import dev.marcocattaneo.core.testing.anyValue
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import dev.marcocattaneo.sleep.player.presentation.player.AudioController
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import kotlin.time.Duration.Companion.seconds

internal class PlayerPresenterTest {

    private companion object {
        const val FAKE_ID = "89e2a967-6b4b-4590-8471-a266feb13b3a"
        const val FAKE_NAME = "My Track"
        const val FAKE_DESCRIPTION = "My Tracks' description"
    }

    @RelaxedMockK
    lateinit var audioController: AudioController

    @RelaxedMockK
    lateinit var mediaRepository: MediaRepository

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)
    }

    private fun getPresenter(eventsChannel: Channel<PlayerEvent>): Flow<PlayerState> {
        return moleculeFlow(RecompositionMode.Immediate) {
            PlayerPresenter(
                audioController,
                mediaRepository
            ).models(events = eventsChannel.receiveAsFlow())
        }
    }

    @Test
    fun `Test PlayerAction StartPlaying`() = runTest {
        // Given
        val events = Channel<PlayerEvent>()
        coEvery { mediaRepository.urlFromId(any()) } returns Either.Right("https://resource")

        getPresenter(events).test {
            // When
            events.send(
                PlayerEvent.StartPlaying(
                    id = FAKE_ID,
                    name = FAKE_NAME,
                    description = FAKE_DESCRIPTION
                )
            )

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Ready>(awaitItem())

            coVerify { audioController.stop() }
            coVerify { mediaRepository.urlFromId(any()) }
            coVerify { audioController.start(any(), any(), any()) }
        }
    }

    @Test
    fun `Test PlayerAction StartPlaying upon failure`() = runTest {
        // Given
        val events = Channel<PlayerEvent>()
        coEvery { mediaRepository.urlFromId(any()) } returns Either.Left(AppException.FileNotFound)

        getPresenter(events).test {
            // When
            events.send(
                PlayerEvent.StartPlaying(
                    id = FAKE_ID,
                    name = FAKE_NAME,
                    description = FAKE_DESCRIPTION
                )
            )

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Error>(awaitItem())

            coVerify { audioController.stop() }
            coVerify { mediaRepository.urlFromId(any()) }
            coVerify(exactly = 0) { audioController.start(any(), any(), any()) }
        }
    }

    @Test
    fun `Test PlayerAction Stop`() = runTest {
        // Given
        val events = Channel<PlayerEvent>()

        getPresenter(events).test {
            // When
            events.send(PlayerEvent.Stop)

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            coVerify { audioController.stop() }
        }
    }

    @Test
    fun `Test PlayerAction Pause`() = runTest {
        // Given
        val events = Channel<PlayerEvent>()

        getPresenter(events).test {
            // When
            events.send(
                PlayerEvent.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )
            events.send(PlayerEvent.Pause)

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            awaitItem().let { state ->
                assertIs<PlayerState.Ready>(state)
                assertEquals(PlayerState.Status.Playing, state.status)
            }
            awaitItem().let { state ->
                assertIs<PlayerState.Ready>(state)
                assertEquals(PlayerState.Status.Paused, state.status)
            }
            coVerify { audioController.pause() }
        }
    }

    @Test
    fun `Test PlayerAction Pause and Play`() = runTest {
        // Given
        val events = Channel<PlayerEvent>()

        getPresenter(events).test {
            // When
            events.send(
                PlayerEvent.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )
            events.send(PlayerEvent.Pause)
            events.send(PlayerEvent.Play)

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Ready>(awaitItem())
            awaitItem().let { state ->
                assertIs<PlayerState.Ready>(state)
                assertEquals(PlayerState.Status.Paused, state.status)
            }
            awaitItem().let { state ->
                assertIs<PlayerState.Ready>(state)
                assertEquals(PlayerState.Status.Playing, state.status)
            }
            coVerify { audioController.pause() }
            coVerify { audioController.play() }
        }
    }

    @Test
    fun `Test PlayerAction UpdateDuration`() = runTest {
        // Given
        val events = Channel<PlayerEvent>()

        getPresenter(events).test {
            // When
            events.send(
                PlayerEvent.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            awaitItem().let {
                assertIs<PlayerState.Ready>(it)

                assertEquals("title", it.trackTitle)
                assertEquals(100L, it.duration.inWholeSeconds)
                assertEquals(20L, it.position.inWholeSeconds)
                assertNull(it.stopTimer)
            }
        }
    }

    @Test
    fun `Test PlayerAction Seeking`() = runTest {
        // Given
        val events = Channel<PlayerEvent>()

        getPresenter(events).test {
            // When
            events.send(
                PlayerEvent.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )
            events.send(PlayerEvent.SeekTo(42.seconds))

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Ready>(awaitItem())
            verify { audioController.seekTo(anyValue()) }
        }
    }

    @Test
    fun `Test PlayerAction ForwardOf`() = runTest {
        // Given
        val events = Channel<PlayerEvent>()

        getPresenter(events).test {
            // When
            events.send(
                PlayerEvent.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )
            events.send(PlayerEvent.ForwardOf)

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Ready>(awaitItem())
            verify { audioController.forwardOf(anyValue()) }
        }
    }

    @Test
    fun `Test PlayerAction ReplayOf`() = runTest {
        // Given
        val events = Channel<PlayerEvent>()

        getPresenter(events).test {
            // When
            events.send(
                PlayerEvent.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )
            events.send(PlayerEvent.ReplayOf)

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Ready>(awaitItem())
            coVerify { audioController.replayOf(anyValue()) }
        }
    }

    @Test
    fun `Test PlayerAction PropagateError`() = runTest {
        // Given
        val events = Channel<PlayerEvent>()

        getPresenter(events).test {
            // When
            events.send(
                PlayerEvent.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )
            events.send(PlayerEvent.PropagateError(500))

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Ready>(awaitItem())
            assertIs<PlayerState.Error>(awaitItem())
        }
    }
}
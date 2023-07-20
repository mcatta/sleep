package dev.marcocattaneo.sleep.player.presentation.screen

import app.cash.turbine.test
import arrow.core.Either
import dev.marcocattaneo.core.testing.anyValue
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import dev.marcocattaneo.sleep.player.presentation.AudioPlayer
import dev.marcocattaneo.sleep.player.presentation.fakeMediaFile
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import kotlin.time.Duration.Companion.seconds

internal class PlayerStateStoreTest {

    @RelaxedMockK
    lateinit var audioPlayer: AudioPlayer

    @RelaxedMockK
    lateinit var playlistStateStore: PlaylistStateStore

    @RelaxedMockK
    lateinit var mediaRepository: MediaRepository

    private lateinit var playerStateStore: PlayerStateStore

    private lateinit var testCoroutineScope: CoroutineScope

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)
        testCoroutineScope = CoroutineScope(Dispatchers.Unconfined)
        playerStateStore = PlayerStateStore(testCoroutineScope, audioPlayer, mediaRepository, playlistStateStore)
    }

    @Test
    fun `Test PlayerAction StartPlaying`() = runTest {
        // Given
        coEvery { mediaRepository.urlFromId(any()) } returns Either.Right("https://resource")

        playerStateStore.stateFlow.test {
            // When
            playerStateStore.dispatchAction(PlayerAction.StartPlaying(fakeMediaFile()))

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Ready>(awaitItem())

            coVerify { audioPlayer.stop() }
            coVerify { mediaRepository.urlFromId(any()) }
            coVerify { audioPlayer.start(any(), any(), any()) }
            coVerify { playlistStateStore.dispatchAction(ofType<PlaylistAction.Update>()) }
        }
    }

    @Test
    fun `Test PlayerAction StartPlaying upon failure`() = runTest {
        // Given
        coEvery { mediaRepository.urlFromId(any()) } returns Either.Left(AppException.FileNotFound)

        playerStateStore.stateFlow.test {
            // When
            playerStateStore.dispatchAction(PlayerAction.StartPlaying(fakeMediaFile()))

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Error>(awaitItem())

            coVerify { audioPlayer.stop() }
            coVerify { mediaRepository.urlFromId(any()) }
            coVerify(exactly = 0) { audioPlayer.start(any(), any(), any()) }
            coVerify(exactly = 0) { playlistStateStore.dispatchAction(ofType<PlaylistAction.Update>()) }
        }
    }

    @Test
    fun `Test PlayerAction Stop`() = runTest {
        playerStateStore.stateFlow.test {
            // When
            playerStateStore.dispatchAction(PlayerAction.Stop)

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            coVerify { audioPlayer.stop() }
        }
    }

    @Test
    fun `Test PlayerAction Pause`() = runTest {
        playerStateStore.stateFlow.test {
            // When
            playerStateStore.dispatchAction(
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )
            playerStateStore.dispatchAction(PlayerAction.Pause)

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Ready>(awaitItem())
            awaitItem().let { state ->
                assertIs<PlayerState.Ready>(state)
                assertEquals(PlayerState.Status.Paused, state.status)
            }
            coVerify { audioPlayer.pause() }
        }
    }

    @Test
    fun `Test PlayerAction Pause and Play`() = runTest {
        playerStateStore.stateFlow.test {
            // When
            playerStateStore.dispatchAction(
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )
            playerStateStore.dispatchAction(PlayerAction.Pause)
            playerStateStore.dispatchAction(PlayerAction.Play)

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
            coVerify { audioPlayer.pause() }
            coVerify { audioPlayer.play() }
        }
    }

    @Test
    fun `Test PlayerAction UpdateDuration`() = runTest {
        playerStateStore.stateFlow.test {
            // When
            playerStateStore.dispatchAction(
                PlayerAction.UpdatePlayerStatus(
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
        playerStateStore.stateFlow.test {
            // When
            playerStateStore.dispatchAction(
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )
            playerStateStore.dispatchAction(PlayerAction.SeekTo(42.seconds))

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Ready>(awaitItem())
            verify { audioPlayer.seekTo(anyValue()) }
        }
    }

    @Test
    fun `Test PlayerAction ForwardOf`() = runTest {
        playerStateStore.stateFlow.test {
            // When
            playerStateStore.dispatchAction(
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )
            playerStateStore.dispatchAction(PlayerAction.ForwardOf)

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Ready>(awaitItem())
            verify { audioPlayer.forwardOf(anyValue()) }
        }
    }

    @Test
    fun `Test PlayerAction ReplayOf`() = runTest {
        playerStateStore.stateFlow.test {
            // When
            playerStateStore.dispatchAction(
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )
            playerStateStore.dispatchAction(PlayerAction.ReplayOf)

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Ready>(awaitItem())
            coVerify { audioPlayer.replayOf(anyValue()) }
        }
    }

    @Test
    fun `Test PlayerAction PropagateError`() = runTest {
        playerStateStore.stateFlow.test {
            // When
            playerStateStore.dispatchAction(
                PlayerAction.UpdatePlayerStatus(
                    duration = 100.seconds,
                    position = 20.seconds,
                    playing = true,
                    trackTitle = "title"
                )
            )
            playerStateStore.dispatchAction(PlayerAction.PropagateError(500))

            // Then
            assertIs<PlayerState.Idle>(awaitItem())
            assertIs<PlayerState.Ready>(awaitItem())
            assertIs<PlayerState.Error>(awaitItem())
        }
    }
}
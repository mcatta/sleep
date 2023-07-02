package dev.marcocattaneo.sleep.ui.screen.home

import app.cash.turbine.test
import arrow.core.Either
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

internal class HomeStateStoreTest {

    @MockK
    lateinit var mediaRepository: MediaRepository

    private lateinit var testCoroutineScope: CoroutineScope

    private lateinit var homeStateStore: HomeStateStore

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)
        testCoroutineScope = CoroutineScope(Dispatchers.Unconfined)
        homeStateStore = HomeStateStore(
            coroutineScope = testCoroutineScope,
            mediaRepository = mediaRepository
        )
    }

    @Test
    fun `Test loading upon failure`() = runTest {
        // Given
        coEvery { mediaRepository.listMedia(any()) } returns Either.Left(AppException.GenericError)

        homeStateStore.stateFlow.test {
            // When
            homeStateStore.dispatchAction(TracksAction.SetLoading)
            homeStateStore.dispatchAction(TracksAction.LoadTracks)

            // Then
            assertIs<TracksState.Loading>(awaitItem())
            assertIs<TracksState.Error>(awaitItem())

            coVerify { mediaRepository.listMedia(any()) }
        }
    }

    @Test
    fun `Test loading upon success`() = runTest {
        // Given
        coEvery { mediaRepository.listMedia(any()) } returns Either.Right(emptyList())

        homeStateStore.stateFlow.test {
            // When
            homeStateStore.dispatchAction(TracksAction.SetLoading)
            homeStateStore.dispatchAction(TracksAction.LoadTracks)

            // Then
            assertIs<TracksState.Loading>(awaitItem())
            assertIs<TracksState.Content>(awaitItem())

            coVerify { mediaRepository.listMedia(any()) }
        }
    }

    @Test
    fun `Test loading upon failure and reload`() = runTest {
        // Given
        coEvery { mediaRepository.listMedia(any()) } returns Either.Left(AppException.GenericError)

        // When
        homeStateStore.stateFlow.test {
            // When
            homeStateStore.dispatchAction(TracksAction.SetLoading)
            homeStateStore.dispatchAction(TracksAction.LoadTracks)

            // Then
            assertIs<TracksState.Loading>(awaitItem())
            assertIs<TracksState.Error>(awaitItem())

            coVerify(exactly = 1) { mediaRepository.listMedia(any()) }
        }
    }

    @Test
    fun `Test reloading`() = runTest {
        // Given
        coEvery { mediaRepository.listMedia(any()) } returns Either.Right(emptyList())

        // When
        homeStateStore.stateFlow.test {
            // When
            homeStateStore.dispatchAction(TracksAction.SetLoading)
            homeStateStore.dispatchAction(TracksAction.LoadTracks)
            homeStateStore.dispatchAction(TracksAction.SetLoading)
            homeStateStore.dispatchAction(TracksAction.LoadTracks)

            // Then
            assertIs<TracksState.Loading>(awaitItem())
            assertIs<TracksState.Content>(awaitItem())
            assertIs<TracksState.Loading>(awaitItem())
            assertIs<TracksState.Content>(awaitItem())

            coVerify(exactly = 2) { mediaRepository.listMedia(any()) }
        }
    }

}
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

package dev.marcocattaneo.sleep.ui.screen.home

import app.cash.turbine.test
import arrow.core.Either
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
internal class HomeStateMachineTest {

    @RelaxedMockK
    lateinit var mediaRepository: MediaRepository

    private lateinit var homeStateMachine: HomeStateMachine

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        homeStateMachine = HomeStateMachine(
            mediaRepository = mediaRepository
        )
    }

    @Test
    fun `Test loading upon failure`() = runTest {
        // Given
        coEvery { mediaRepository.listMedia(any()) } returns Either.Left(AppException.GenericError)

        // When
        homeStateMachine.state.test {
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

        // When
        homeStateMachine.state.test {
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
        homeStateMachine.state.test {
            homeStateMachine.dispatch(TracksAction.Reload)

            // Then
            assertIs<TracksState.Loading>(awaitItem())
            assertIs<TracksState.Error>(awaitItem())
            assertIs<TracksState.Loading>(awaitItem())
            assertIs<TracksState.Error>(awaitItem())

            coVerify(exactly = 2) { mediaRepository.listMedia(any()) }
        }
    }

    @Test
    fun `Test reloading`() = runTest {
        // Given
        coEvery { mediaRepository.listMedia(any()) } returns Either.Right(emptyList())

        // When
        homeStateMachine.state.test {
            homeStateMachine.dispatch(TracksAction.Reload)

            // Then
            assertIs<TracksState.Loading>(awaitItem())
            assertIs<TracksState.Content>(awaitItem())
            assertIs<TracksState.Loading>(awaitItem())
            assertIs<TracksState.Content>(awaitItem())

            coVerify(exactly = 2) { mediaRepository.listMedia(any()) }
        }
    }
}
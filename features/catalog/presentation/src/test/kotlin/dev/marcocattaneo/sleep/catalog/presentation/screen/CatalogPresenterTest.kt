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

package dev.marcocattaneo.sleep.catalog.presentation.screen

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import arrow.core.Either
import arrow.core.left
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.cache.CachePolicy
import dev.marcocattaneo.sleep.domain.model.MediaFileEntity
import dev.marcocattaneo.sleep.domain.model.TrackId
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

internal class CatalogPresenterTest {

    @MockK
    private lateinit var mediaRepository: MediaRepository

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)
    }

    private fun testPresenter(events: Flow<CatalogEvent>): Flow<CatalogState> = moleculeFlow(
        mode = RecompositionMode.Immediate
    ) { CatalogPresenter(mediaRepository).models(events = events) }

    @Test
    fun `Test loading upon failure`() = runTest {
        // Given
        coEvery { mediaRepository.listMedia(any()) } coAnswers {
            delay(1L)
            AppException.GenericError.left()
        }
        val events = Channel<CatalogEvent>()

        testPresenter(events.receiveAsFlow()).test {
            // When
            awaitItem() // Skip initial state
            events.send(CatalogEvent.LoadTracks)

            // Then
            assertIs<CatalogState.Loading>(awaitItem())
            assertIs<CatalogState.Error>(awaitItem())

            coVerify { mediaRepository.listMedia(any()) }
        }
    }

    @Test
    fun `Test loading upon success`() = runTest {
        // Given
        val events = Channel<CatalogEvent>()
        coEvery { mediaRepository.listMedia(any()) } coAnswers {
            delay(1L)
            Either.Right(emptyList())
        }

        // When
        testPresenter(events.receiveAsFlow()).test {
            // Then
            awaitItem() // Skip initial state

            events.send(CatalogEvent.LoadTracks)
            assertIs<CatalogState.Loading>(awaitItem())
            assertIs<CatalogState.Content>(awaitItem())

            coVerify { mediaRepository.listMedia(any()) }
        }
    }

    @Test
    fun `Test reloading`() = runTest {
        // Given
        val events = Channel<CatalogEvent>()
        coEvery { mediaRepository.listMedia(any()) } coAnswers {
            delay(1L)
            Either.Right(emptyList())
        }

        // When
        testPresenter(events.receiveAsFlow()).test {
            // Then
            awaitItem() // Skip initial state

            events.send(CatalogEvent.LoadTracks)
            assertIs<CatalogState.Loading>(awaitItem())
            assertIs<CatalogState.Content>(awaitItem())

            events.send(CatalogEvent.LoadTracks)
            assertIs<CatalogState.Loading>(awaitItem())
            assertIs<CatalogState.Content>(awaitItem())

            coVerify(exactly = 2) { mediaRepository.listMedia(any()) }
        }
    }

}
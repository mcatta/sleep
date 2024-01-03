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

package dev.marcocattaneo.sleep.data.repository

import arrow.core.Either
import dev.marcocattaneo.sleep.data.auth.AuthDataSource
import dev.marcocattaneo.sleep.data.http.SleepService
import dev.marcocattaneo.sleep.data.model.MediaUrl
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.net.SocketException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
internal class MediaRepositoryImplTest {

    private lateinit var mediaRepository: MediaRepository

    @MockK
    lateinit var sleepService: SleepService

    @MockK
    lateinit var authDataSource: AuthDataSource

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)

        coEvery { authDataSource.getAuthToken() } returns Either.Right("token")

        mediaRepository = MediaRepositoryImpl(
            authDataSource = authDataSource,
            sleepService = sleepService,
            baseRepository = BaseRepositoryImpl()
        )
    }

    @Test
    fun `Test urlFromPath upon a success`() = runTest {
        // Given
        coEvery { sleepService.downloadUrl(any(), any()) } returns  MediaUrl("url", "path")

        // When
        val res = mediaRepository.urlFromId("path")

        // Then
        assertEquals(true, res.isRight())
        coVerify { sleepService.downloadUrl(any(), eq("path")) }
    }

    @Test
    fun `Test urlFromPath upon a failure`() = runTest {
        // Given
        coEvery { sleepService.downloadUrl(any(), any()) } throws SocketException("Failure")

        // When
        val res = mediaRepository.urlFromId("path")

        // Then
        assertEquals(true, res.isLeft())
        coVerify { sleepService.downloadUrl(any(), eq("path")) }
    }

}
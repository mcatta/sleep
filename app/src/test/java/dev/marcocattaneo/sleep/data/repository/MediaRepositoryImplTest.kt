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

import android.net.Uri
import arrow.core.Either
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import dev.marcocattaneo.sleep.data.mapper.MediaFileMapper
import dev.marcocattaneo.sleep.data.mapper.mockStorageReference
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@ExperimentalCoroutinesApi
class MediaRepositoryImplTest {

    lateinit var mediaRepository: MediaRepository

    @MockK
    lateinit var firebaseStorage: FirebaseStorage

    @MockK
    lateinit var storageReference: StorageReference

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)

        every { storageReference.child(any()) } returns storageReference
        every { firebaseStorage.reference } returns storageReference

        mediaRepository = MediaRepositoryImpl(
            mediaFileMapper = MediaFileMapper(),
            firebaseStorage = firebaseStorage
        )
    }

    @Test
    fun `Test listMedia upon a success`() = runTest {
        // Given
        val taskMocked = mockk<Task<ListResult>>()
        val slot = slot<OnSuccessListener<ListResult>>()
        val result = mockk<ListResult>()
        every { result.items } returns listOf(mockStorageReference(), mockStorageReference())
        every { taskMocked.addOnSuccessListener(capture(slot)) } answers {
            slot.captured.onSuccess(result)
            taskMocked
        }
        every { taskMocked.addOnFailureListener(any()) } returns taskMocked
        every { storageReference.listAll() } returns taskMocked

        // When
        val res = mediaRepository.listMedia()

        // Then
        assertIs<Either.Right<*>>(res)
        assertEquals(2, (res as Either.Right).value.size)
    }

    @Test
    fun `Test listMedia upon a failure`() = runTest {
        // Given
        val taskMocked = mockk<Task<ListResult>>()
        val slot = slot<OnFailureListener>()
        every { taskMocked.addOnSuccessListener(any()) } returns taskMocked
        every { taskMocked.addOnFailureListener(capture(slot)) } answers {
            slot.captured.onFailure(IllegalStateException("Something goes wrong"))
            taskMocked
        }
        every { storageReference.listAll() } returns taskMocked

        // When
        val res = mediaRepository.listMedia()

        // Then
        assertIs<Either.Left<*>>(res)
    }

    @Test
    fun `Test urlFromPath upon a success`() = runTest {
        // Given
        val taskMocked = mockk<Task<Uri>>()
        val slot = slot<OnSuccessListener<Uri>>()
        every { taskMocked.addOnFailureListener(any()) } returns taskMocked
        every { taskMocked.addOnSuccessListener(capture(slot)) } answers {
            slot.captured.onSuccess(mockk())
            taskMocked
        }
        every { storageReference.downloadUrl } returns taskMocked

        // When
        val res = mediaRepository.urlFromPath("path")

        // Then
        assertIs<Either.Right<*>>(res)
    }

    @Test
    fun `Test urlFromPath upon a failure`() = runTest {
        // Given
        val taskMocked = mockk<Task<Uri>>()
        val slot = slot<OnFailureListener>()
        every { taskMocked.addOnFailureListener(capture(slot)) } answers {
            slot.captured.onFailure(IllegalStateException("Something goes wrong"))
            taskMocked
        }
        every { taskMocked.addOnSuccessListener(any()) } returns taskMocked
        every { storageReference.downloadUrl } returns taskMocked

        // When
        val res = mediaRepository.urlFromPath("path")

        // Then
        assertIs<Either.Left<*>>(res)
    }


}
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

package dev.marcocattaneo.sleep.data.mapper

import com.google.firebase.storage.StorageReference
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals

class MediaFileMapperTest {

    private val mediaFileMapper = MediaFileMapper()

    @Test
    fun `Test MediaFileMapper`() {
        // Given
        val storageReference = mockStorageReference()

        // When
        val mapped = mediaFileMapper.mapTo(storageReference)

        // Then
        assertEquals("name.mp3", mapped.fileName)
        assertEquals("name", mapped.name)
        assertEquals("path", mapped.path)
    }

}

fun mockStorageReference(): StorageReference {
    val ref = mockk<StorageReference>()
    every { ref.name } returns "name.mp3"
    every { ref.path } returns "path"
    return ref
}
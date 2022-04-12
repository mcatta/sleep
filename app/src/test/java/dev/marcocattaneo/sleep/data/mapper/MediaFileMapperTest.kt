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

import com.google.firebase.firestore.DocumentSnapshot
import dev.marcocattaneo.sleep.domain.model.MediaFile
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MediaFileMapperTest {

    private val mediaFileMapper = MediaFileMapper()

    @Test
    fun `Test MediaFileMapper`() {
        // Given
        val storageReference = mockStorageReference()

        // When
        val mapped = mediaFileMapper.mapTo(storageReference)

        // Then
        assertIs<MediaFile>(mapped)
        assertEquals("description", mapped.description)
        assertEquals("name", mapped.name)
        assertEquals("path", mapped.path)
    }

}

fun mockStorageReference(): DocumentSnapshot {
    val ref = mockk<DocumentSnapshot>()
    every { ref.getString("name") } returns "name"
    every { ref.getString("storage") } returns "path"
    every { ref.getString("description") } returns "description"
    return ref
}
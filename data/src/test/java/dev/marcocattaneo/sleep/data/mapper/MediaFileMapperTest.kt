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

import dev.marcocattaneo.sleep.data.model.MediaFile
import dev.marcocattaneo.sleep.domain.model.MediaFileEntity
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MediaFileMapperTest {

    private val mediaFileMapper = MediaFileMapper()

    @Test
    fun `Test MediaFileMapper`() {
        // Given
        val storageReference = MediaFile("UUID", "name", "description", "path")

        // When
        val mapped = mediaFileMapper.mapTo(storageReference)

        // Then
        assertIs<MediaFileEntity>(mapped)
        assertEquals("description", mapped.description)
        assertEquals("UUID", mapped.id)
        assertEquals("name", mapped.name)
        assertEquals("path", mapped.path)
    }

}
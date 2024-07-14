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

package dev.marcocattaneo.sleep.data.cache

import dev.marcocattaneo.sleep.domain.cache.CachePolicy
import dev.marcocattaneo.sleep.domain.cache.CacheService
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds

internal class InMemoryCacheTest {

    private lateinit var cacheService: CacheService<String, String>

    private val cachePolicy = CachePolicy.CacheFirst(30.seconds)

    @BeforeTest
    fun setup() {
        cacheService = InMemoryCache()
    }

    @Test
    fun `Test setValue and getValue`() = runTest {
        // Given
        cacheService.setValue("key", "value", cachePolicy)

        // When
        val value = cacheService.getValue("key", cachePolicy)

        // Then
        assertEquals("value", value)
    }

    @Test
    fun `Test clear`() = runTest {
        // Given
        cacheService.setValue("key", "value", cachePolicy)
        assertEquals("value", cacheService.getValue("key", cachePolicy))

        // When
        cacheService.clear()

        // Then
        assertNull(cacheService.getValue("key", cachePolicy))
    }

}

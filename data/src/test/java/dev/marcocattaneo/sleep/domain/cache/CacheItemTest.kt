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

package dev.marcocattaneo.sleep.domain.cache

import dev.marcocattaneo.sleep.domain.model.sec
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CacheItemTest {

    @Test
    fun `Test a CacheItem with policy Never`() {
        // Given
        val cacheItem = CacheItem.of("Hello")

        // When
        val value = cacheItem.getIfValid(CachePolicy.Never)

        // Then
        assertNull(value)
    }

    @Test
    fun `Test a CacheItem with policy CacheFirst`() {
        // Given
        val cacheItem = CacheItem.of("Hello", createdAt = System.nanoTime() - 100_000)

        // When
        val value = cacheItem.getIfValid(CachePolicy.CacheFirst(30.sec))

        // Then
        assertNotNull(value)
    }

    @Test
    fun `Test a CacheItem with policy RefreshAndCache`() {
        // Given
        val cacheItem = CacheItem.of("Hello")

        // When
        val value = cacheItem.getIfValid(CachePolicy.RefreshAndCache)

        // Then
        assertNull(value)
    }

}
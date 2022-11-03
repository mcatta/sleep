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

package dev.marcocattaneo.sleep.ui.composables

import dev.marcocattaneo.sleep.domain.model.sec
import org.junit.Assert.*
import kotlin.test.Test

class ComposableExtTest {

    @Test
    fun `Test Seconds formatter`() {
        // Given
        val seconds = 30.sec

        // When / Then
        assertEquals("00:30", seconds.format())
    }


    @Test
    fun `Test Minutes formatter`() {
        // Given
        val seconds = 125.sec

        // When / Then
        assertEquals("02:05", seconds.format())
    }


    @Test
    fun `Test Hours formatter`() {
        // Given
        val seconds = (60 * 60).sec

        // When / Then
        assertEquals("01:00:00", seconds.format())
    }

}
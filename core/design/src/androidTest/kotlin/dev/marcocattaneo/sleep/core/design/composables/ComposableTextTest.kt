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

package dev.marcocattaneo.sleep.core.design.composables

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import dev.marcocattaneo.core.design.composables.*
import dev.marcocattaneo.sleep.core.design.AbsComposableTest
import org.junit.Test

internal const val SAMPLE_TEXT = "Hello Text"

internal class ComposableTextTest : AbsComposableTest() {

    @Test
    fun testBody1() {
        // Given
        composeTestRule.setContentWithTheme {
            Body1(text = SAMPLE_TEXT)
        }

        // Then
        composeTestRule.onNodeWithText(SAMPLE_TEXT)
            .assertIsDisplayed()
    }

    @Test
    fun testBody2() {
        // Given
        composeTestRule.setContentWithTheme {
            Body2(text = SAMPLE_TEXT)
        }

        // Then
        composeTestRule.onNodeWithText(SAMPLE_TEXT)
            .assertIsDisplayed()
    }

    @Test
    fun testH4() {
        // Given
        composeTestRule.setContentWithTheme {
            H4(text = SAMPLE_TEXT)
        }

        // Then
        composeTestRule.onNodeWithText(SAMPLE_TEXT)
            .assertIsDisplayed()
    }

    @Test
    fun testCaption() {
        // Given
        composeTestRule.setContentWithTheme {
            Caption(text = SAMPLE_TEXT)
        }

        // Then
        composeTestRule.onNodeWithText(SAMPLE_TEXT)
            .assertIsDisplayed()
    }

    @Test
    fun testOverLine() {
        // Given
        composeTestRule.setContentWithTheme {
            OverLine(text = SAMPLE_TEXT)
        }

        // Then
        composeTestRule.onNodeWithText(SAMPLE_TEXT)
            .assertIsDisplayed()
    }

}
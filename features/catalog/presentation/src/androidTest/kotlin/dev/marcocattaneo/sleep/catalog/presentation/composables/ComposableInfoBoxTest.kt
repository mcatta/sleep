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

package dev.marcocattaneo.sleep.catalog.presentation.composables

import androidx.compose.material.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import dev.marcocattaneo.sleep.catalog.presentation.AbsComposableTest
import org.junit.Test

internal class ComposableInfoBoxTest : AbsComposableTest() {

    @Test
    fun testInfoBox() {
        // Given
        composeTestRule.setContentWithTheme {
            InfoBox {
                Text(text = "Hello InfoBox")
            }
        }

        composeTestRule.onNodeWithText("Hello InfoBox")
            .assertIsDisplayed()
    }
}
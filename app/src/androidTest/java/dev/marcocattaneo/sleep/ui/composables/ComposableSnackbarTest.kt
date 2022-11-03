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

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import dev.marcocattaneo.sleep.AbsComposableTest
import org.junit.Test

internal class ComposableSnackbarTest : AbsComposableTest() {

    @Test
    fun testSnackbar() {
        // Given
        composeTestRule.setContentWithTheme {
            Snackbar(message = "Hello snackbar")
        }

        // When / Then
        composeTestRule.onNodeWithText("Hello snackbar")
            .assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Error icon")
            .assertIsDisplayed()
    }

}
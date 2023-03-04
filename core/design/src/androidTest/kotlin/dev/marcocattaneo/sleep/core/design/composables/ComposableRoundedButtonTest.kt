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

import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.onNodeWithContentDescription
import dev.marcocattaneo.core.design.composables.RoundedButton
import dev.marcocattaneo.sleep.core.design.AbsComposableTest
import org.junit.Test

internal class ComposableRoundedButtonTest : AbsComposableTest() {


    @Test
    fun testRoundedButton() {
        // Given
        val contentDesc = "Composable text"

        composeTestRule.setContentWithTheme {
            RoundedButton(onClick = {}) {
                Text(text = "Button text", modifier = Modifier.semantics { contentDescription = contentDesc })
            }
        }

        // When / Then
        composeTestRule.onNodeWithContentDescription(contentDesc)
            .assertExists(errorMessageOnFail = "The node with $contentDesc does not exists")
    }

}
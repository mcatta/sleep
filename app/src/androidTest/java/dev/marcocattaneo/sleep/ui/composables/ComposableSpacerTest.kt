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

import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onRoot
import dev.marcocattaneo.sleep.AbsComposableTest
import dev.marcocattaneo.sleep.ui.theme.Dimen
import org.junit.Test

internal class ComposableSpacerTest : AbsComposableTest() {

    @Test
    fun testSpacer32() {
        // Given
        composeTestRule.setContentWithTheme {
            Spacer32()
        }

        composeTestRule.onRoot()
            .assertIsDisplayed()
            .assertHeightIsAtLeast(Dimen.Margin32)
    }

    @Test
    fun testSpacer16() {
        // Given
        composeTestRule.setContentWithTheme {
            Spacer16()
        }

        composeTestRule.onRoot()
            .assertIsDisplayed()
            .assertHeightIsAtLeast(Dimen.Margin16)
    }

    @Test
    fun testSpacer8() {
        // Given
        composeTestRule.setContentWithTheme {
            Spacer8()
        }

        composeTestRule.onRoot()
            .assertIsDisplayed()
            .assertHeightIsAtLeast(Dimen.Margin8)
    }
}
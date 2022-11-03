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

import androidx.compose.ui.test.*
import dev.marcocattaneo.sleep.AbsComposableTest
import dev.marcocattaneo.sleep.domain.model.sec
import org.junit.Test

internal class ComposablePlayerTest : AbsComposableTest() {

    @Test
    fun testPlayerInit() {
        // Given
        composeTestRule.setContentWithTheme {
            BottomPlayerBar(
                isPlaying = true,
                duration = 120.sec,
                position = 20.sec,
                onChangePlayingStatus = {},
                onChangeStopTimer = {},
                onClickReplay = {},
                onClickForward = {},
                onClickStop = {},
                onSeeking = {}
            )
        }

        // When / Then
        composeTestRule.onNodeWithText("02:00")  // duration
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("00:20")  // position
            .assertIsDisplayed()
    }


    @Test
    fun testPlayerStopToggle() {
        // Given
        composeTestRule.setContentWithTheme {
            BottomPlayerBar(
                isPlaying = true,
                duration = 120.sec,
                position = 20.sec,
                onChangePlayingStatus = {},
                onChangeStopTimer = {},
                onClickReplay = {},
                onClickForward = {},
                onClickStop = {},
                onSeeking = {}
            )
        }

        // When / Then

        // Show timer
        composeTestRule.onNodeWithContentDescription("Change timer")
            .performClick()
        composeTestRule.onNodeWithText("10m")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("20m")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("30m")
            .assertIsDisplayed()

        // Hide Timer
        composeTestRule.onNodeWithContentDescription("Change timer")
            .performClick()
        composeTestRule.onNodeWithText("10m")
            .assertDoesNotExist()
    }

}
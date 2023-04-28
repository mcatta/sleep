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

package dev.marcocattaneo.sleep.player.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.marcocattaneo.sleep.player.presentation.AbsComposableTest
import org.junit.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal class ComposableBottomPlayerBarTest : AbsComposableTest() {

    @Test
    fun testPlayerInit() {
        // Given
        composeTestRule.setContentWithTheme {
            BottomPlayerBar(
                isPlaying = true,
                duration = 120.seconds,
                position = 20.seconds,
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
                duration = 120.seconds,
                position = 20.seconds,
                supportedStoppingTimeframes = setOf(10.minutes, 20.minutes, 30.minutes),
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
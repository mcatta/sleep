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

package dev.marcocattaneo.sleep

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import dev.marcocattaneo.core.design.theme.SleepTheme
import org.junit.Rule

internal abstract class AbsComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    protected fun ComposeContentTestRule.setContentWithTheme(content: @Composable () -> Unit) = this.setContent {
        SleepTheme(content = content)
    }

}
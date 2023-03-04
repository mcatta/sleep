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

package dev.marcocattaneo.sleep.core.utils

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.marcocattaneo.core.testing.CoroutinesTestRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
internal class AbsStateMachineViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutinesTestRule()

    @Test
    fun `Test dispatch on AbsStateMachineViewModel`() = coroutineTestRule.scope.runTest {
        // Given
        val mockedStateMachine = mockk<FlowReduxStateMachine<String, String>>()
        coEvery { mockedStateMachine.dispatch(any()) } just Runs
        val testViewModel = object : AbsStateMachineViewModel<String, String>(mockedStateMachine) {}

        // When
        testViewModel.dispatch("action")

        // Then
        coVerify { mockedStateMachine.dispatch(eq("action")) }
    }

}
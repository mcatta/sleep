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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mcatta.polpetta.StateStore
import dev.mcatta.polpetta.operators.Action
import dev.mcatta.polpetta.operators.State
import kotlinx.coroutines.launch

abstract class AbsStateStoreViewModel <A : Action, S : State>(
    private val stateStore: StateStore<A, S>
) : ViewModel() {

    @Composable
    fun rememberState() = stateStore.stateFlow.collectAsState()


    fun dispatch(action: A) = viewModelScope.launch {
        stateStore.dispatchAction(action)
    }

}
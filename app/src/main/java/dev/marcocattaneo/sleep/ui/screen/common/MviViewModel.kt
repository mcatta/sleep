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

package dev.marcocattaneo.sleep.ui.screen.common

import androidx.lifecycle.viewModelScope
import dev.marcocattaneo.mvi.State
import dev.marcocattaneo.mvi.intent.Action
import dev.marcocattaneo.mvi.intent.IntentFactory
import dev.marcocattaneo.mvi.store.Store
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class MviViewModel<S : State, A : Action> constructor(
    private val store: Store<S>,
    private val intentFactory: IntentFactory<S, A>
) : BaseViewModel() {

    val uiState: StateFlow<S>
        get() = store.stateFlow

    fun process(action: A) {
        viewModelScope.launch { store.process(intentFactory.buildIntent(action)) }
    }

}
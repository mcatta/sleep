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

package dev.marcocattaneo.asmrelax.ui.screen.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class StateViewModel<UiState> constructor(
    initialState: UiState
): BaseViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<UiState>
        get() = _uiState

    suspend fun emitState(block: (state: UiState) -> UiState) {
        _uiState.emit(block(uiState.value))
    }
}
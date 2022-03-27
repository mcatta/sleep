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

package dev.marcocattaneo.mvi.intent

import dev.marcocattaneo.mvi.State
import dev.marcocattaneo.mvi.store.Store

/**
 * Intent factory used to create an Intent starting from an Action
 * @param store store instance related to the intent factory [Store]
 */
abstract class IntentFactory<S: State, A: Action>(
    private val store: Store<S>
) {

    abstract suspend fun buildIntent(action: A): Intent<S>

    suspend fun handleSideEffectAction(action: A) = store.process(buildIntent(action))

}
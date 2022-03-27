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

fun interface Intent<S : State> {
    suspend fun reduce(oldState: S): S
}

/**
 * Create an intent that reduces the state into a new one
 * @param block lambda function used to reduce the state
 * @return [Intent]
 */
fun <S : State> intent(
    block: suspend S.() -> S
) = Intent<S> { oldState -> block(oldState) }


/**
 * Create a side-effect that generate a new Action, but also support the state reducing before
 * @param sideEffectBlock side-effect block
 * @return [Intent]
 */
suspend fun <S : State, A : Action> IntentFactory<S, A>.sideEffect(
    sideEffectBlock: suspend () -> A
) = Intent<S> { it }.also {
    handleSideEffectAction(sideEffectBlock())
}
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

package dev.marcocattaneo.sleep.core.di

import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.marcocattaneo.sleep.core.di.scope.CoroutineMainScope
import dev.marcocattaneo.sleep.core.di.scope.MoleculeComposableScope
import dev.marcocattaneo.sleep.core.di.scope.MoleculeRecompositionMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoroutineModule {

    @CoroutineMainScope
    @Singleton
    @Provides
    fun provideCoroutineMainScope(): CoroutineScope = CoroutineScope(Dispatchers.Main)

    @MoleculeComposableScope
    @Singleton
    @Provides
    fun provideMoleculeComposableScope(): CoroutineScope = CoroutineScope(AndroidUiDispatcher.Main)

    @MoleculeRecompositionMode
    @Singleton
    @Provides
    fun provideMoleculeRecompositionMode(): RecompositionMode = RecompositionMode.ContextClock
}
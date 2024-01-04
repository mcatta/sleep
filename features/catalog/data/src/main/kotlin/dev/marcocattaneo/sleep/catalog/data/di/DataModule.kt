/*
 * Copyright 2024 Marco Cattaneo
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

package dev.marcocattaneo.sleep.catalog.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.marcocattaneo.sleep.catalog.data.repository.CatalogRepositoryImpl
import dev.marcocattaneo.sleep.catalog.domain.model.MediaFileEntity
import dev.marcocattaneo.sleep.catalog.domain.repository.CatalogRepository
import dev.marcocattaneo.sleep.data.cache.InMemoryCache
import dev.marcocattaneo.sleep.domain.cache.CacheService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun provideCatalogRepository(catalogRepositoryImpl: CatalogRepositoryImpl): CatalogRepository

}

@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {

    @Provides
    @Singleton
    fun provideMediaFileCache(): CacheService<String, List<MediaFileEntity>> = InMemoryCache()

}
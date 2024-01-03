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

package dev.marcocattaneo.sleep.catalog.data.repository

import arrow.core.Either
import arrow.core.raise.either
import dev.marcocattaneo.sleep.catalog.domain.model.MediaFileEntity
import dev.marcocattaneo.sleep.catalog.domain.repository.CatalogRepository
import dev.marcocattaneo.sleep.data.auth.AuthDataSource
import dev.marcocattaneo.sleep.data.http.SleepService
import dev.marcocattaneo.sleep.catalog.data.mapper.MediaFileMapper
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.cache.CachePolicy
import dev.marcocattaneo.sleep.domain.cache.CacheService
import dev.marcocattaneo.sleep.domain.repository.BaseRepository
import javax.inject.Inject

class CatalogRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val sleepService: SleepService,
    private val mediaFileMapper: MediaFileMapper,
    private val mediaFileCache: CacheService<String, List<MediaFileEntity>>,
    baseRepository: BaseRepository
) : CatalogRepository, BaseRepository by baseRepository {

    companion object {
        private const val MEDIA_FILE_CACHE_KEY = "media_files"
    }

    override suspend fun listMedia(cachePolicy: CachePolicy): Either<AppException, List<MediaFileEntity>> {
        return either {
            val token = authDataSource.getAuthToken().bind()

            handleCachedValue(
                cacheKey = MEDIA_FILE_CACHE_KEY,
                cachePolicy = cachePolicy,
                cacheService = mediaFileCache
            ) {
                sleepService
                    .tracks("Bearer $token")
                    .map(mediaFileMapper::mapTo)
            }.bind()
        }
    }
}
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

package dev.marcocattaneo.sleep.data.repository

import arrow.core.Either
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.cache.CachePolicy
import dev.marcocattaneo.sleep.domain.cache.CacheService
import dev.marcocattaneo.sleep.domain.repository.BaseRepository
import java.lang.Exception
import javax.inject.Inject

internal class BaseRepositoryImpl @Inject constructor() : BaseRepository {

    override suspend fun <K : Any, V : Any> handleCachedValue(
        cacheService: CacheService<K, V>,
        cacheKey: K,
        cachePolicy: CachePolicy,
        block: suspend () -> V
    ): Either<AppException, V> = eitherResult {
        cacheService.getValue(cacheKey, cachePolicy) ?: block().also { value ->
            cacheService.setValue(cacheKey, value, cachePolicy)
        }
    }

    override suspend fun <V : Any> handleValue(
        block: suspend () -> V
    ): Either<AppException, V> = eitherResult(block)

    private suspend fun <T : Any> eitherResult(
        block: suspend () -> T
    ): Either<AppException, T> {
        return try {
            Either.Right(block())
        } catch (e: Exception) {
            Either.Left(AppException.GenericError)
        }
    }

}
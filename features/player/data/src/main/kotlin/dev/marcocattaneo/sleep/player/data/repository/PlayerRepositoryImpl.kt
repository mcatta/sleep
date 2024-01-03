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

package dev.marcocattaneo.sleep.player.data.repository

import arrow.core.Either
import arrow.core.raise.either
import dev.marcocattaneo.sleep.data.auth.AuthDataSource
import dev.marcocattaneo.sleep.data.http.SleepService
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.repository.BaseRepository
import dev.marcocattaneo.sleep.player.domain.repository.PlayerRepository
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val sleepService: SleepService,
    baseRepository: BaseRepository
) : PlayerRepository, BaseRepository by baseRepository {

    override suspend fun urlFromId(id: String): Either<AppException, String> {
        return either {
            val token = authDataSource.getAuthToken().bind()

            handleValue {
                sleepService.downloadUrl(
                    authorization = "Bearer $token",
                    id = id
                )
            }.map { it.url }.bind()
        }
    }
}
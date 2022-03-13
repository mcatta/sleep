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

package dev.marcocattaneo.asmrelax.domain.repository

import android.net.Uri
import arrow.core.Either
import dev.marcocattaneo.asmrelax.domain.AppException
import dev.marcocattaneo.asmrelax.domain.model.MediaFile
import dev.marcocattaneo.asmrelax.domain.model.Path

interface MediaRepository {

    suspend fun listMedia(): Either<AppException, List<MediaFile>>

    suspend fun urlFromPath(path: Path): Either<AppException, Uri>

}
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

package dev.marcocattaneo.sleep.catalog.data.mapper

import dev.marcocattaneo.sleep.catalog.domain.model.MediaFileEntity
import dev.marcocattaneo.sleep.data.model.MediaFile
import dev.marcocattaneo.sleep.domain.mapper.Mapper
import javax.inject.Inject

class MediaFileMapper @Inject constructor(): Mapper<MediaFile, MediaFileEntity> {

    override fun mapTo(from: MediaFile) = MediaFileEntity(
        id = from.id,
        description = from.description,
        name = from.name,
        path = from.path
    )
}
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

package dev.marcocattaneo.sleep.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import dev.marcocattaneo.sleep.domain.mapper.Mapper
import dev.marcocattaneo.sleep.domain.model.MediaFile
import javax.inject.Inject

class MediaFileMapper @Inject constructor(): Mapper<DocumentSnapshot, MediaFile> {
    override fun mapTo(from: DocumentSnapshot) = MediaFile(
        id = from.id,
        description = from.getString("description"),
        name = from.getString("name") ?: "",
        path = from.getString("storage") ?: ""
    )
}
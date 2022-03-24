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

package dev.marcocattaneo.asmrelax.data.repository

import android.net.Uri
import arrow.core.Either
import com.google.firebase.storage.FirebaseStorage
import dev.marcocattaneo.asmrelax.data.mapper.MediaFileMapper
import dev.marcocattaneo.asmrelax.domain.AppException
import dev.marcocattaneo.asmrelax.domain.model.MediaFile
import dev.marcocattaneo.asmrelax.domain.model.Path
import dev.marcocattaneo.asmrelax.domain.repository.MediaRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class MediaRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val mediaFileMapper: MediaFileMapper
) : MediaRepository {

    companion object {
        const val AUDIO_FOLDER = "audio"
    }

    private val mediaStorageRef = firebaseStorage.reference.child(AUDIO_FOLDER)

    override suspend fun listMedia(): Either<AppException, List<MediaFile>> =
        suspendCancellableCoroutine { continuation ->
            mediaStorageRef.listAll()
                .addOnSuccessListener { listResult ->
                    listResult.items
                        .map(mediaFileMapper::mapTo)
                        .let { list -> continuation.resume(Either.Right(list)) }
                }
                .addOnFailureListener {
                    continuation.resume(Either.Left(AppException.GenericError))
                }
        }

    override suspend fun urlFromPath(
        path: Path
    ): Either<AppException, Uri> = suspendCancellableCoroutine { continuation ->
        firebaseStorage
            .reference
            .child(path)
            .downloadUrl
            .addOnSuccessListener { url -> continuation.resume(Either.Right(url)) }
            .addOnFailureListener { continuation.resume(Either.Left(AppException.GenericError)) }
    }
}
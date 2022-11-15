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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import dev.marcocattaneo.sleep.data.mapper.MediaFileMapper
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.cache.CachePolicy
import dev.marcocattaneo.sleep.domain.cache.CacheService
import dev.marcocattaneo.sleep.domain.model.MediaFile
import dev.marcocattaneo.sleep.domain.model.Path
import dev.marcocattaneo.sleep.domain.repository.BaseRepository
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class MediaRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseFirestore: FirebaseFirestore,
    private val mediaFileMapper: MediaFileMapper,
    private val mediaFileCache: CacheService<String, List<MediaFile>>,
    baseRepository: BaseRepository
) : MediaRepository, BaseRepository by baseRepository {

    companion object {
        const val AUDIO_COLLECTION = "audio"

        private const val MEDIA_FILE_CACHE_KEY = "media_files"
    }

    override suspend fun listMedia(cachePolicy: CachePolicy): Either<AppException, List<MediaFile>> {
        return handleCachedValue(
            cacheKey = MEDIA_FILE_CACHE_KEY,
            cachePolicy = cachePolicy,
            cacheService = mediaFileCache
        ) {
            suspendCancellableCoroutine { continuation ->
                firebaseFirestore.collection(AUDIO_COLLECTION)
                    .orderBy("order", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener { listResult ->
                        listResult.documents
                            .map(mediaFileMapper::mapTo)
                            .let { list -> continuation.resume(Either.Right(list)) }
                    }
                    .addOnFailureListener {
                        continuation.resume(Either.Left(AppException.GenericError))
                    }
            }
        }
    }

    override suspend fun urlFromPath(
        path: Path
    ): Either<AppException, String> = suspendCancellableCoroutine { continuation ->
        firebaseStorage
            .reference
            .child(path)
            .downloadUrl
            .addOnSuccessListener { url -> continuation.resume(Either.Right(url.toString())) }
            .addOnFailureListener { continuation.resume(Either.Left(AppException.FileNotFound)) }
    }
}
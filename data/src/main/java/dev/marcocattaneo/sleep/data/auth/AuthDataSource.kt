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

package dev.marcocattaneo.sleep.data.auth

import arrow.core.Either
import arrow.core.flatMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dev.marcocattaneo.sleep.domain.AppException
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

internal class AuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun getAuthToken(): Either<AppException, String> =
        getCurrentUser().flatMap { user ->
            getUserToken(user)
        }

    private suspend fun getUserToken(user: FirebaseUser): Either<AppException, String> =
        suspendCancellableCoroutine { continuation ->
            user.getIdToken(true)
                .addOnSuccessListener { result ->
                    result.token?.let {
                        continuation.resume(Either.Right(it))
                    } ?: kotlin.run {
                        continuation.resume(Either.Left(AppException.GenericError))
                    }
                }
                .addOnFailureListener {
                    continuation.resume(Either.Left(AppException.GenericError))
                }
        }

    /**
     * Return the current FirebaseUser (Anonymous)
     */
    private suspend fun getCurrentUser(): Either<AppException, FirebaseUser> =
        suspendCancellableCoroutine { continuation ->
            firebaseAuth
                .signInAnonymously()
                .addOnSuccessListener {
                    it.user?.let { user ->
                        continuation.resume(Either.Right(user))
                    } ?: kotlin.run {
                        continuation.resume(Either.Left(AppException.GenericError))
                    }
                }
                .addOnFailureListener {
                    continuation.resume(Either.Left(AppException.GenericError))
                }
        }

}
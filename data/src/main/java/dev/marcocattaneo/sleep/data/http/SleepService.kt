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

package dev.marcocattaneo.sleep.data.http

import dev.marcocattaneo.sleep.data.model.MediaFile
import dev.marcocattaneo.sleep.data.model.MediaUrl
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SleepService {

    @GET("media")
    suspend fun tracks(@Header("Authorization") authorization: String): List<MediaFile>

    @GET("media/url")
    suspend fun downloadUrl(
        @Header("Authorization") authorization: String,
        @Query("path") path: String
    ): MediaUrl

}
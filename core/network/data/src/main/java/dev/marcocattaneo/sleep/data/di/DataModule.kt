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

package dev.marcocattaneo.sleep.data.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.marcocattaneo.sleep.data.BuildConfig
import dev.marcocattaneo.sleep.data.cache.InMemoryCache
import dev.marcocattaneo.sleep.data.http.SleepService
import dev.marcocattaneo.sleep.data.repository.BaseRepositoryImpl
import dev.marcocattaneo.sleep.data.repository.MediaRepositoryImpl
import dev.marcocattaneo.sleep.domain.cache.CacheService
import dev.marcocattaneo.sleep.domain.model.MediaFileEntity
import dev.marcocattaneo.sleep.domain.repository.BaseRepository
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun provideMediaRepository(mediaRepositoryImpl: MediaRepositoryImpl): MediaRepository

    @Binds
    @Singleton
    abstract fun provideBaseRepository(baseRepository: BaseRepositoryImpl): BaseRepository

}

@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(
            OkHttpClient.Builder()
                .apply {
                    if (BuildConfig.DEBUG)
                        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideService(
        retrofit: Retrofit
    ): SleepService = retrofit.create(SleepService::class.java)

    @Provides
    @Singleton
    fun provideMediaFileCache(): CacheService<String, List<MediaFileEntity>> =
        InMemoryCache()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}
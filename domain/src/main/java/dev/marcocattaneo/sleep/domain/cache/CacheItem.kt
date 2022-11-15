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

package dev.marcocattaneo.sleep.domain.cache

class CacheItem<T : Any> private constructor(
    private val value: T,
    private val createdAt: Long
) {

    companion object {
        fun <T : Any> of(
            value: T,
            createdAt: Long = System.nanoTime()
        ) = CacheItem(value, createdAt)
    }

    fun getIfValid(cachePolicy: CachePolicy): T? = when (cachePolicy) {
        is CachePolicy.CacheFirst -> if (createdAt + cachePolicy.expireIn.value > System.nanoTime()) null else value
        CachePolicy.Never,
        CachePolicy.RefreshAndCache -> null
    }

}
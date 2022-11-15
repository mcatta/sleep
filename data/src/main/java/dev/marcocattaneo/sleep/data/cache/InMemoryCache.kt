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

package dev.marcocattaneo.sleep.data.cache

import dev.marcocattaneo.sleep.domain.cache.CacheItem
import dev.marcocattaneo.sleep.domain.cache.CachePolicy
import dev.marcocattaneo.sleep.domain.cache.CacheService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryCache<V : Any> : CacheService<String, V> {

    private val _mutex = Mutex()

    private var _valueMap: MutableMap<String, CacheItem<V>?> = mutableMapOf()

    override suspend fun getValue(
        key: String,
        cachePolicy: CachePolicy
    ): V? = _mutex.withLock {
        _valueMap[key]?.getIfValid(cachePolicy)
    }

    override suspend fun setValue(
        key: String,
        value: V,
        cachePolicy: CachePolicy
    ) {
        _mutex.withLock {
            when (cachePolicy) {
                is CachePolicy.CacheFirst,
                CachePolicy.RefreshAndCache -> {
                    _valueMap.put(key, CacheItem.of(value))
                }

                CachePolicy.Never -> Unit
            }
        }
    }


    override suspend fun clear() {
        _mutex.withLock { _valueMap.clear() }
    }

}
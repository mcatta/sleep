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

import kotlin.time.Duration

sealed interface CachePolicy {
    // No caching
    object Never : CachePolicy

    // Make the call and cache the value
    object RefreshAndCache : CachePolicy

    // Return the cached value if valid
    class CacheFirst(val expireIn: Duration) : CachePolicy {
        init {
            check(expireIn.inWholeNanoseconds > 0) { "expireIn must be greater than 0" }
        }
    }
}
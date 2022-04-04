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

package dev.marcocattaneo.sleep.domain.model

sealed interface TimeUnit

data class Seconds(val value: Long): TimeUnit
data class Minutes(val value: Long): TimeUnit

inline val Int.sec: Seconds get() = this.toLong().sec

inline val Int.min: Minutes get() = this.toLong().min

inline val Long.sec: Seconds get() = Seconds(this)

inline val Long.min: Minutes get() = Minutes(this)
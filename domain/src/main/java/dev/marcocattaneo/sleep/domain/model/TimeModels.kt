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

sealed class TimeUnit constructor(val value: Long): Comparable<TimeUnit> {
    override fun equals(other: Any?): Boolean = (other as? Minutes)?.value == this.value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = value.toString()
    override fun compareTo(other: TimeUnit): Int = when {
        value == other.value -> 0
        value < other.value -> -1
        value > other.value -> 1
        else -> throw IllegalStateException("Unsupported compare operation")
    }
}

class Seconds(value: Long): TimeUnit(value)
class Minutes(value: Long): TimeUnit(value)

/**
 * Smart constructors
 */

inline val Int.sec: Seconds get() = this.toLong().sec

inline val Int.min: Minutes get() = this.toLong().min

inline val Long.sec: Seconds get() = Seconds(this)

inline val Long.min: Minutes get() = Minutes(this)
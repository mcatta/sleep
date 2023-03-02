/*
 * Copyright 2023 Marco Cattaneo
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

package sleep.buildtools.utils

import org.gradle.api.Project


/**
 * Returns an int value based on the key [key].
 * @param key
 * @return the value
 * @throws IllegalArgumentException in case the parameter does not exist
 */
@Throws(IllegalArgumentException::class)
fun Project.getIntProperty(key: String): Int = properties.getOrElse(key) {
    throw IllegalArgumentException("The property with key: $key does not exist")
}.toString().toInt()

/**
 * Returns an int value based on the key [key].
 * @param key
 * @return the value
 * @throws IllegalArgumentException in case the parameter does not exist
 */
@Throws(IllegalArgumentException::class)
fun Project.getBooleanProperty(key: String): Boolean = properties.getOrElse(key) {
    throw IllegalArgumentException("The property with key: $key does not exist")
}.toString().toBoolean()
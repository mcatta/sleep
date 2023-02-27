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
import org.gradle.api.reflect.TypeOf.typeOf

/**
 * Configure a plugin [T] on the [Project] by using the configuration [configuration]
 * @param configuration
 */
@Suppress("deprecation")
inline fun <reified T : Any> Project.configure(
    noinline configuration: T.() -> Unit
) {
    typeOf(T::class.java).let { type ->
        convention.findByType(type)?.let(configuration)
            ?: convention.findPlugin(T::class.java)?.let(configuration)
            ?: convention.configure(type, configuration)
    }
}
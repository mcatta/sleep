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

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

/**
 * Register a new Task with name [name] and configuration [configuration]
 * @param name
 * @param configuration
 */
inline fun <reified T : Task> TaskContainer.register(
    name: String,
    noinline configuration: T.() -> Unit
): TaskProvider<T> {
    return register(name, T::class.java, configuration)
}

/**
 * Configure a task of type [T] by using the [configuration]
 * @param configuration
 */
inline fun <reified T : Any> Task.configure(
    noinline configuration: T.() -> Unit
) {
    return this.extensions.configure(T::class.java, configuration)
}
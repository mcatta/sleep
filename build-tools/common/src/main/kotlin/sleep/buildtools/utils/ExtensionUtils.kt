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

import org.gradle.api.plugins.ExtensionContainer

/**
 * Invokes Groovy's [ExtensionContainer.getByType] reifying the type [T].
 * @author @fondesa
 */
inline fun <reified T : Any> ExtensionContainer.getByType(): T = getByType(T::class.java)

/**
 * Invokes Groovy's [ExtensionContainer.configure] reifying the type [T].
 * @author @fondesa
 */
inline fun <reified T : Any> ExtensionContainer.configure(noinline action: (T) -> Unit) {
    configure(T::class.java, action)
}

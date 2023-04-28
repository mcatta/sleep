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

package dev.marcocattaneo.core.testing

import io.mockk.ConstantMatcher
import io.mockk.Matcher
import io.mockk.MockKGateway
import io.mockk.MockKMatcherScope
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

/**
 * Use this function instead of [any()] when the argument is a value class
 *
 * @see <a href="https://github.com/mockk/mockk/issues/152">Issue 152</a>
 */
inline fun <reified T : Any> MockKMatcherScope.anyValue(): T =
    if (T::class.isValue) {
        anyInline()
    } else {
        any()
    }

inline fun <reified T : Any> MockKMatcherScope.anyInline(): T =
    T::class.primaryConstructor!!.run {
        this.isAccessible = true
        val valueType = parameters[0].type.classifier as KClass<*>
        call(match(ConstantMatcher(true), valueType))
    }

fun <T : Any> MockKMatcherScope.match(matcher: Matcher<T>, type: KClass<T>): T =
    (getProperty("callRecorder") as MockKGateway.CallRecorder).matcher(matcher, type)

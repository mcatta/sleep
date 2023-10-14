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

package dev.marcocattaneo.navigation.routing

import android.net.Uri
import androidx.navigation.navArgument

/**
 * Screen Route definition
 * @param routeDefinition route name used by NavigationController
 */
abstract class ScreenRoute(
    val routeDefinition: Definition
) {

    data class Definition(
        private val routePath: String,
        val argumentKeys: List<Pair<String, ArgumentOptions.() -> Unit>> = listOf()
    ) {

        /**
         * Return the routing path used to register the navigation route, it contains also params
         * in the format /routePath?key1={key1}&key2={key2}
         * @return path
         */
        fun getRoutePath() = uriBuilder(routePath) {
            argumentKeys.iterator().forEach { key ->
                appendQueryParameter(key.first, "{${key.first}}")
            }
            this
        }

        /**
         * Returns the list of navArgument with type NavType.StringType
         */
        fun getNavArguments() = argumentKeys.map {
            val arguments = ArgumentOptions().apply(it.second)
            navArgument(it.first) {
                type = arguments.type
                if (arguments.optional) {
                    nullable = true
                    defaultValue = null
                }
            }
        }

        /**
         * Generate URI from path
         * @param path URI path
         * @param block lambda used to append queries
         * @return encoded URI
         */
        private fun uriBuilder(path: String, block: (Uri.Builder).() -> Uri.Builder): String =
            block(Uri.Builder().appendEncodedPath(path))
                .build()
                .toString()
                .removeRange(0..0)

    }
}
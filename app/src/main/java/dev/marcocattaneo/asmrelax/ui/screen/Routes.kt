/*
 * Copyright 2021 Marco Cattaneo
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

package dev.marcocattaneo.asmrelax.ui.screen

import androidx.navigation.NavType
import dev.marcocattaneo.asmrelax.navigation.routing.ScreenRoute
import dev.marcocattaneo.asmrelax.ui.screen.RouteKeys.PATH_KEY

object Routes {

    object Login : ScreenRoute(
        routeDefinition = Definition("login")
    )

    object Player : ScreenRoute(
        routeDefinition = Definition("player", argumentKeys = listOf(
            PATH_KEY to { type = NavType.StringType; optional = false }
        ))
    )

}

object RouteKeys {
    const val PATH_KEY = "path"
}
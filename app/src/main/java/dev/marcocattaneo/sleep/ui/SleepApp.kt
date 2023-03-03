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

package dev.marcocattaneo.sleep.ui

import android.content.res.Configuration
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dev.marcocattaneo.core.design.theme.SleepTheme
import dev.marcocattaneo.sleep.navigation.NavigationComponent
import dev.marcocattaneo.sleep.navigation.NavigationControllerImpl
import dev.marcocattaneo.sleep.navigation.composable
import dev.marcocattaneo.sleep.ui.screen.Routes
import dev.marcocattaneo.sleep.ui.screen.home.HomeScreen
import dev.marcocattaneo.sleep.ui.screen.home.HomeViewModel
import dev.marcocattaneo.sleep.player.presentation.screen.PlayerAction
import dev.marcocattaneo.sleep.player.presentation.screen.PlayerScreen
import dev.marcocattaneo.sleep.player.presentation.screen.PlayerViewModel

@Composable
fun SleepApp() {
    val navHostState = rememberNavController()
    val controller = NavigationControllerImpl(navHostState)
    val configuration = LocalConfiguration.current

    SleepTheme {
        Surface(color = MaterialTheme.colors.background) {
            val playerViewModel = hiltViewModel<PlayerViewModel>()

            PlayerScreen(
                playerViewModel = playerViewModel,
                isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            ) {
                NavigationComponent(
                    startRoute = Routes.Login,
                    navigationController = controller
                ) {

                    composable<HomeViewModel>(
                        route = Routes.Login
                    ) { _, vm ->
                        HomeScreen(vm, onClickMediaFile = {
                            playerViewModel.dispatch(PlayerAction.StartPlaying(it))
                        })
                    }

                    // ... other routes

                }
            }
        }
    }
}
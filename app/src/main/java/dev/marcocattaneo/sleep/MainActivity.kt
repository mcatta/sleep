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

package dev.marcocattaneo.sleep

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.marcocattaneo.sleep.navigation.NavigationComponent
import dev.marcocattaneo.sleep.navigation.NavigationControllerImpl
import dev.marcocattaneo.sleep.navigation.composable
import dev.marcocattaneo.sleep.ui.notification.PlayerNotificationService
import dev.marcocattaneo.sleep.ui.screen.Routes
import dev.marcocattaneo.sleep.ui.screen.home.HomeScreen
import dev.marcocattaneo.sleep.ui.screen.home.HomeViewModel
import dev.marcocattaneo.sleep.ui.screen.player.PlayerAction
import dev.marcocattaneo.sleep.ui.screen.player.PlayerScreen
import dev.marcocattaneo.sleep.ui.screen.player.PlayerViewModel
import dev.marcocattaneo.sleep.ui.theme.SleepTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start NotificationService
        Intent(this, PlayerNotificationService::class.java)
            .let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(it)
                } else {
                    startService(it)
                }
            }

        setContent {
            val navHostState = rememberNavController()
            val controller = NavigationControllerImpl(navHostState)
            SleepTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val playerViewModel = hiltViewModel<PlayerViewModel>()

                    PlayerScreen(
                        playerViewModel
                    ) {
                        NavigationComponent(
                            startRoute = Routes.Login,
                            navigationController = controller
                        ) {

                            composable<HomeViewModel>(
                                route = Routes.Login,
                                navigationController = controller
                            ) { _, vm ->
                                HomeScreen(vm, onClickMediaFile = {
                                    playerViewModel.process(PlayerAction.InitPlayer(it))
                                })
                            }

                            // ... other routes

                        }
                    }
                }
            }
        }
    }
}
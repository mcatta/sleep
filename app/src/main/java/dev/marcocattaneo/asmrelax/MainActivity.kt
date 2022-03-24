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

package dev.marcocattaneo.asmrelax

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.marcocattaneo.asmrelax.navigation.NavigationComponent
import dev.marcocattaneo.asmrelax.navigation.NavigationControllerImpl
import dev.marcocattaneo.asmrelax.navigation.composable
import dev.marcocattaneo.asmrelax.ui.notification.PlayerNotificationService
import dev.marcocattaneo.asmrelax.ui.screen.Routes
import dev.marcocattaneo.asmrelax.ui.screen.home.HomeViewModel
import dev.marcocattaneo.asmrelax.ui.screen.home.HomeScreen
import dev.marcocattaneo.asmrelax.ui.screen.player.PlayerScreen
import dev.marcocattaneo.asmrelax.ui.screen.player.PlayerViewModel
import dev.marcocattaneo.asmrelax.ui.theme.AndroidcomposetemplateTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start NotificationService
        startService(Intent(this, PlayerNotificationService::class.java))

        setContent {
            val navHostState = rememberNavController()
            val controller = NavigationControllerImpl(navHostState)
            AndroidcomposetemplateTheme {
                Surface(color = MaterialTheme.colors.background) {
                    NavigationComponent(
                        startRoute = Routes.Login,
                        navigationController = controller
                    ) {

                        composable<HomeViewModel>(
                            route = Routes.Login,
                            navigationController = controller
                        ) { _, vm ->
                            HomeScreen(vm)
                        }

                        composable<PlayerViewModel>(
                            route = Routes.Player,
                            navigationController = controller
                        ) { _, vm ->
                            PlayerScreen(vm)
                        }
                    }
                }
            }
        }
    }
}
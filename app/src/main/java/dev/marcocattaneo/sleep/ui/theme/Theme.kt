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

package dev.marcocattaneo.sleep.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorPalette = lightColors(
    primary = Cherry200,
    primaryVariant = Cherry700,
    secondary = Garden200,
    secondaryVariant = Garden900,
    background = CandyPaper50,
    surface = CandyPaper0,
    onPrimary = DirtyWhite,
    onSecondary = VolcanicStone,
    onBackground = VolcanicStone,
    onSurface = VolcanicStone,
    error = Cherry500,
    onError = DirtyWhite
)

private val DarkColorPalette = darkColors(
    primary = Cherry200,
    primaryVariant = Cherry700,
    secondary = Garden200,
    secondaryVariant = Garden200,
    background = Mud900,
    surface = Mud800,
    onPrimary = DirtyWhite,
    onSecondary = VolcanicStone,
    onBackground = Mud50,
    onSurface = Mud50,
    error = Cherry600,
    onError = DirtyWhite
)

@Composable
fun SleepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    systemUiController.setSystemBarsColor(
        color = colors.surface
    )

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
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

package dev.marcocattaneo.sleep.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.marcocattaneo.sleep.domain.model.Seconds

@Composable
fun screenWidth(): Float {
    val context = LocalContext.current.resources
    val displayMetrics = context.displayMetrics
    return displayMetrics.widthPixels / displayMetrics.density
}

fun Seconds.format(): String {
    val hours = this.value.div(3_600)
    val minutes = (this.value % 3600) / 60
    val seconds = this.value % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
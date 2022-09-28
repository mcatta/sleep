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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import dev.marcocattaneo.sleep.R
import dev.marcocattaneo.sleep.ui.theme.Dimen
import dev.marcocattaneo.sleep.ui.theme.SleepTheme

@Composable
fun Snackbar(
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colors.error)
            .padding(all = Dimen.Margin16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_error_outline_24),
            contentDescription = "Error icon",
            tint = MaterialTheme.colors.onError
        )
        Text(
            text = message,
            color = MaterialTheme.colors.onError,
            modifier = Modifier.padding(start = Dimen.Margin8)
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
internal fun SnackbarPreview() {
    SleepTheme {
        Snackbar(modifier = Modifier.fillMaxWidth(), message = "Hello Snackbar")
    }
}
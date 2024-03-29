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

package dev.marcocattaneo.sleep.catalog.presentation.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.marcocattaneo.core.design.theme.Dimen.Margin16

@Composable
fun InfoBox(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row(
            modifier = Modifier.padding(all = Margin16),
            content = content
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
private fun InfoBoxPreview() {
    InfoBox {
        Text(text = "Hello, InfoBox")
    }
}
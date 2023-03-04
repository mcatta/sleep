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

package dev.marcocattaneo.core.design.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Body1(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.typography.body1.color
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = MaterialTheme.typography.body1
    )
}

@Composable
fun Body2(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.typography.body2.color
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = MaterialTheme.typography.body2
    )
}

@Composable
fun H4(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.typography.caption.color
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = MaterialTheme.typography.h4
    )
}

@Composable
fun Caption(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.typography.caption.color
) {
    Text(
        modifier = modifier,
        color = color,
        text = text,
        style = MaterialTheme.typography.caption
    )
}

@Composable
fun OverLine(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.typography.overline.color
) {
    Text(
        modifier = modifier,
        color = color,
        text = text,
        style = MaterialTheme.typography.overline
    )
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
fun TestPreview() {
    Column {
        H4(text = "H4")
        Body1(text = "Body1")
        Body2(text = "Body2")
        Caption(text = "Caption")
        OverLine(text = "OverLine")
    }
}
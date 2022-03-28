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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.marcocattaneo.sleep.domain.model.PlayerSeconds
import dev.marcocattaneo.sleep.ui.theme.Dimen.Margin16
import dev.marcocattaneo.sleep.ui.theme.Dimen.Margin8

/**
 * @param modifier
 * @param isPlaying true if is playing
 * @param duration duration in seconds
 * @param position position in seconds
 * @param onChangePlayingStatus
 */
@Composable
fun BottomPlayerBar(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    duration: PlayerSeconds,
    position: PlayerSeconds,
    onChangePlayingStatus: (Boolean) -> Unit
) {
    val screenWidth = screenWidth()

    Card(
        modifier = Modifier
            .padding(Margin16)
            .fillMaxWidth()
            .then(modifier),
        elevation = 10.dp,
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { onChangePlayingStatus(!isPlaying) },
                    modifier = Modifier
                        .padding(top = Margin16)
                        .background(
                            color = MaterialTheme.colors.secondaryVariant,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colors.background,
                        imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.Close,
                        contentDescription = null
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Margin8, horizontal = Margin16)
                ) {
                    Body1(
                        text = position.format(),
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Body1(
                        text = duration.format(),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.secondary.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width((screenWidth * position).div(duration).dp)
                            .background(MaterialTheme.colors.secondaryVariant)
                            .clip(RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    )
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
fun BottomPlayerBarPreview() {
    BottomPlayerBar(
        isPlaying = true,
        duration = 36000,
        position = 5500
    ) {}
}
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

import android.graphics.ColorSpace
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.marcocattaneo.sleep.R
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

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SeekBar(duration = duration, position = position)
        IconButton(
            onClick = { onChangePlayingStatus(!isPlaying) },
            modifier = Modifier
                .padding(all = Margin16)
                .background(
                    color = MaterialTheme.colors.secondaryVariant,
                    shape = CircleShape
                )
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .padding(Margin8),
                tint = MaterialTheme.colors.background,
                painter = painterResource(id = if (isPlaying) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun SeekBar(
    position: PlayerSeconds,
    duration: PlayerSeconds
) {
    val screenWidth = screenWidth()
    val progressWidth = (screenWidth * position).div(duration)
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Margin8)
        ) {
            OverLine(
                text = position.format(),
                modifier = Modifier.align(Alignment.CenterStart)
            )
            OverLine(
                text = duration.format(),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(4.dp)
                .width(progressWidth.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colors.secondaryVariant)
        )
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(screenWidth.minus(progressWidth).dp)
                .background(MaterialTheme.colors.secondaryVariant.copy(alpha = 0.4f))
        )
    }
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
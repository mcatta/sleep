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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.marcocattaneo.sleep.R
import dev.marcocattaneo.sleep.domain.model.Minutes
import dev.marcocattaneo.sleep.domain.model.Seconds
import dev.marcocattaneo.sleep.domain.model.min
import dev.marcocattaneo.sleep.domain.model.sec
import dev.marcocattaneo.sleep.ui.composables.animations.CollapseAnimation
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
    duration: Seconds,
    position: Seconds,
    onChangePlayingStatus: (Boolean) -> Unit,
    onChangeStopTimer: (Minutes) -> Unit
) {
    var timerVisible by remember { mutableStateOf(false) }
    val onClickTimerButton: (Minutes) -> Unit =  {
        onChangeStopTimer(it)
        timerVisible = false
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SeekBar(duration = duration, position = position)
        Box(modifier = Modifier.fillMaxWidth()) {
            RoundedButton(
                modifier = Modifier
                    .padding(all = Margin16)
                    .align(Alignment.TopCenter),
                onClick = { onChangePlayingStatus(!isPlaying) }
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
            RoundedButton(
                backgroundColor = Color.Transparent,
                modifier = Modifier
                    .padding(all = Margin8)
                    .align(Alignment.CenterEnd),
                onClick = { timerVisible = !timerVisible }
            ) {
                Icon(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(Margin8),
                    tint = MaterialTheme.colors.onBackground,
                    painter = painterResource(id = R.drawable.ic_baseline_access_alarm_24),
                    contentDescription = null
                )
            }
        }
        CollapseAnimation(
            visible = timerVisible
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Margin16),
                horizontalArrangement = Arrangement.Center
            ) {
                RoundedButton(
                    onClick = { onClickTimerButton(30.min) },
                    content = { Caption(text = "30m", color = Color.White) }
                )
                Spacer8()
                RoundedButton(
                    onClick = { onClickTimerButton(60.min) },
                    content = { Caption(text = "60m", color = Color.White) }
                )
            }
        }
    }
}

@Composable
private fun SeekBar(
    position: Seconds,
    duration: Seconds
) {
    val screenWidth = screenWidth()
    val progressWidth = (screenWidth * position.value).div(duration.value)
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
                .background(MaterialTheme.colors.primary)
        )
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(screenWidth.minus(progressWidth).dp)
                .background(MaterialTheme.colors.primary.copy(alpha = 0.4f))
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
fun BottomPlayerBarPreview() {
    BottomPlayerBar(
        isPlaying = true,
        duration = 36000.sec,
        position = 5500.sec,
        onChangePlayingStatus = {},
        onChangeStopTimer = {}
    )
}
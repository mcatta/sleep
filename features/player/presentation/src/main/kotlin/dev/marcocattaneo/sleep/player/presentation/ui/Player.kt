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

package dev.marcocattaneo.sleep.player.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.marcocattaneo.core.design.composables.*
import dev.marcocattaneo.core.design.theme.Dimen.Margin16
import dev.marcocattaneo.core.design.theme.Dimen.Margin32
import dev.marcocattaneo.core.design.theme.Dimen.Margin8
import dev.marcocattaneo.sleep.player.presentation.R as PlayerR
import dev.marcocattaneo.sleep.core.design.R as DesignR
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * @param modifier
 * @param isPlaying true if is playing
 * @param duration duration in seconds
 * @param position position in seconds
 * @param selectedStopTimer selected stop timeframe
 * @param supportedStoppingTimeframes supported timeframes
 * @param onChangePlayingStatus
 */
@Composable
fun BottomPlayerBar(
    modifier: Modifier = Modifier,
    description: String = "",
    isPlaying: Boolean,
    duration: Duration,
    position: Duration,
    selectedStopTimer: Duration? = null,
    supportedStoppingTimeframes: Set<Duration> = SleepAppConstants.SUPPORTED_STOP_TIME_FRAMES,
    onChangePlayingStatus: (Boolean) -> Unit,
    onChangeStopTimer: (Duration) -> Unit,
    onClickReplay: () -> Unit,
    onClickForward: () -> Unit,
    onClickStop: () -> Unit,
    onSeeking: (Duration) -> Unit
) {
    var timerVisible by remember { mutableStateOf(false) }
    val onClickTimerButton: (Duration) -> Unit = {
        onChangeStopTimer(it)
        timerVisible = false
    }

    Card(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .padding(top = Margin16)
                .then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (description.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = DesignR.drawable.ic_baseline_play_circle_outline_24),
                        contentDescription = description,
                        modifier = Modifier.size(Margin16),
                        tint = MaterialTheme.colors.primary
                    )
                    Spacer4()
                    Caption(
                        text = description,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            SeekBar(duration = duration, position = position, onSeeking = onSeeking)
            // Buttons
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(all = Margin16)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionButton(
                        painter = painterResource(id = PlayerR.drawable.ic_baseline_replay_30_24),
                        internalMargin = 4.dp,
                        onClick = onClickReplay
                    )
                    Spacer8()
                    PlayButton(
                        isPlaying = isPlaying,
                        onChangePlayingStatus = onChangePlayingStatus
                    )
                    Spacer8()
                    ActionButton(
                        painter = painterResource(id = PlayerR.drawable.ic_baseline_forward_30_24),
                        internalMargin = 4.dp,
                        onClick = onClickForward
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(all = Margin8)
                        .align(Alignment.CenterEnd)
                ) {
                    selectedStopTimer?.let { minutes ->
                        Text(
                            modifier = Modifier
                                .padding(top = Margin32)
                                .align(Alignment.TopEnd),
                            style = TextStyle.Default.copy(fontSize = 8.sp),
                            text = minutes.toString()
                        )
                    }
                    ActionButton(
                        painter = painterResource(id = PlayerR.drawable.ic_baseline_access_alarm_24),
                        modifier = Modifier.semantics { contentDescription = "Change timer" },
                        onClick = { timerVisible = !timerVisible }
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(all = Margin8)
                        .align(Alignment.CenterStart)
                ) {
                    ActionButton(
                        painter = painterResource(id = PlayerR.drawable.ic_baseline_close_24),
                        onClick = onClickStop
                    )
                }
            }

            // StopAt selection
            dev.marcocattaneo.core.design.animations.CollapseAnimation(
                visible = timerVisible
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Margin16),
                    horizontalArrangement = Arrangement.Center
                ) {
                    supportedStoppingTimeframes.iterator().forEach { timeFrame ->
                        RoundedButton(
                            modifier = Modifier.padding(horizontal = Margin8),
                            onClick = { onClickTimerButton(timeFrame) },
                            content = {
                                Caption(
                                    text = "${timeFrame.inWholeMinutes}m",
                                    color = if (selectedStopTimer == timeFrame)
                                        MaterialTheme.colors.onSecondary
                                    else
                                        MaterialTheme.colors.onPrimary
                                )
                            },
                            backgroundColor = if (selectedStopTimer == timeFrame)
                                MaterialTheme.colors.secondary
                            else
                                MaterialTheme.colors.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayButton(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    onChangePlayingStatus: (Boolean) -> Unit
) {
    RoundedButton(
        modifier = modifier,
        onClick = { onChangePlayingStatus(!isPlaying) }
    ) {
        Icon(
            modifier = Modifier
                .size(48.dp)
                .padding(Margin8),
            tint = MaterialTheme.colors.background,
            painter = painterResource(id = if (isPlaying) PlayerR.drawable.ic_baseline_pause_24 else PlayerR.drawable.ic_baseline_play_arrow_24),
            contentDescription = null
        )
    }
}

@Composable
private fun ActionButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    internalMargin: Dp = Margin8,
    onClick: () -> Unit
) {
    RoundedButton(
        modifier = modifier,
        backgroundColor = Color.Transparent,
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier
                .size(32.dp)
                .padding(internalMargin),
            tint = MaterialTheme.colors.primary,
            painter = painter,
            contentDescription = null
        )
    }
}

@Composable
private fun SeekBar(
    position: Duration,
    duration: Duration,
    onSeeking: (Duration) -> Unit
) {
    // To avoid division by zero
    val progress = position.inWholeSeconds.toFloat().div(max(duration.inWholeSeconds.toFloat(), 1f))

    // To handle dragging
    var isSeekingInProgress by remember { mutableStateOf(false) }
    var selectedValue by remember { mutableStateOf(0f) }
    fun getValue() = if (isSeekingInProgress) selectedValue else progress

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Margin8)
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
    Slider(
        modifier = Modifier.fillMaxWidth(),
        value = getValue(),
        onValueChange = {
            isSeekingInProgress = true
            selectedValue = it
            onSeeking((duration.inWholeSeconds * it).toInt().seconds)
        },
        onValueChangeFinished = { isSeekingInProgress = false },
        colors = SliderDefaults.colors(
            activeTrackColor = MaterialTheme.colors.primaryVariant,
            inactiveTrackColor = MaterialTheme.colors.primaryVariant.copy(alpha = 0.4f)
        )
    )
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
fun BottomPlayerBarPreview() {
    BottomPlayerBar(
        isPlaying = true,
        duration = 360300.seconds,
        position = 5500.seconds,
        selectedStopTimer = 30.minutes,
        onChangePlayingStatus = {},
        onChangeStopTimer = {},
        onClickForward = {},
        onClickReplay = {},
        onSeeking = {},
        onClickStop = {}
    )
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
fun BottomPlayerBarWithDescriptionPreview() {
    BottomPlayerBar(
        description = "My track name",
        isPlaying = true,
        duration = 360300.seconds,
        position = 5500.seconds,
        selectedStopTimer = 30.minutes,
        onChangePlayingStatus = {},
        onChangeStopTimer = {},
        onClickForward = {},
        onClickReplay = {},
        onSeeking = {},
        onClickStop = {}
    )
}
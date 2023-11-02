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

package dev.marcocattaneo.sleep.player.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import dev.marcocattaneo.core.design.animations.CollapseAnimation
import dev.marcocattaneo.core.design.composables.Snackbar
import dev.marcocattaneo.core.design.theme.Dimen.Margin16
import dev.marcocattaneo.core.design.theme.Dimen.Margin2
import dev.marcocattaneo.core.design.theme.Dimen.Margin8
import dev.marcocattaneo.sleep.player.presentation.R
import dev.marcocattaneo.sleep.player.presentation.ui.BottomPlayerBar
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayerScreen(
    playerViewModel: PlayerViewModel,
    isLandscape: Boolean,
    content: @Composable (ColumnScope.() -> Unit),
) {
    val uiState by playerViewModel.uiState.collectAsState()

    if (isLandscape) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom) {
            Column(
                modifier = Modifier.weight(0.6f),
                content = content
            )
            PlayerController(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(Margin8),
                uiState = uiState,
                playerViewModel = playerViewModel
            )
        }
    } else {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (container, player) = createRefs()

            Column(
                modifier = Modifier.constrainAs(container) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(player.top)
                    height = Dimension.fillToConstraints
                },
                content = content
            )
            PlayerController(
                modifier = Modifier.constrainAs(player) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.wrapContent
                },
                uiState = uiState,
                playerViewModel = playerViewModel
            )
        }
    }
}

@Composable
private fun PlayerController(
    uiState: PlayerState?,
    playerViewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val isVisible = uiState is PlayerState.Ready
    val position: Duration
    val duration: Duration
    val isPlaying: Boolean
    val stopTimer: Duration?
    val trackTitle: String
    when (uiState) {
        is PlayerState.Ready -> uiState.let {
            position = it.position
            duration = it.duration
            isPlaying = it.status == PlayerState.Status.Playing
            stopTimer = it.stopTimer
            trackTitle = it.trackTitle
        }

        else -> {
            position = 0.seconds
            duration = 0.seconds
            isPlaying = false
            stopTimer = null
            trackTitle = ""
        }
    }
    Column(
        modifier = modifier
    ) {
        CollapseAnimation(
            visible = uiState is PlayerState.Error,
        ) {
            Snackbar(
                message = stringResource(id = R.string.player_error_occurred),
                modifier = Modifier.fillMaxWidth()
            )
        }
        CollapseAnimation(
            visible = isVisible,
        ) {
            BottomPlayerBar(
                modifier = Modifier.padding(horizontal = Margin16, vertical = Margin2),
                position = position,
                description = trackTitle,
                duration = duration,
                isPlaying = isPlaying,
                selectedStopTimer = stopTimer,
                onChangePlayingStatus = { isPlaying ->
                    if (isPlaying) {
                        playerViewModel.dispatchEvent(PlayerEvent.Play)
                    } else {
                        playerViewModel.dispatchEvent(PlayerEvent.Pause)
                    }
                },
                onChangeStopTimer = {
                    playerViewModel.dispatchEvent(PlayerEvent.StopAfter(it))
                },
                onClickReplay = { playerViewModel.dispatchEvent(PlayerEvent.ReplayOf) },
                onClickForward = { playerViewModel.dispatchEvent(PlayerEvent.ForwardOf) },
                onSeeking = { playerViewModel.dispatchEvent(PlayerEvent.SeekTo(it)) },
                onClickStop = { playerViewModel.dispatchEvent(PlayerEvent.Stop) }
            )
        }
    }
}
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

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
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
    val uiState by playerViewModel.rememberState()

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
    val isVisible = uiState is PlayerState.Playing || uiState is PlayerState.Pause || uiState is PlayerState.Init
    val position: Duration
    val duration: Duration
    val isPlaying: Boolean
    val stopTimer: Duration?
    when (uiState) {
        is PlayerState.Pause,
        is PlayerState.Playing -> (uiState as PlayerState.CommonPlayingState).let {
            position = it.position
            duration = it.duration
            isPlaying = uiState is PlayerState.Playing
            stopTimer = it.stopTimer
        }

        else -> {
            position = 0.seconds
            duration = 0.seconds
            isPlaying = false
            stopTimer = null
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
                duration = duration,
                isPlaying = isPlaying,
                selectedStopTimer = stopTimer,
                onChangePlayingStatus = { isPlaying ->
                    if (isPlaying) {
                        playerViewModel.dispatch(PlayerAction.Play)
                    } else {
                        playerViewModel.dispatch(PlayerAction.Pause)
                    }
                },
                onChangeStopTimer = {
                    playerViewModel.dispatch(PlayerAction.StopAfter(it))
                },
                onClickReplay = { playerViewModel.dispatch(PlayerAction.ReplayOf) },
                onClickForward = { playerViewModel.dispatch(PlayerAction.ForwardOf) },
                onSeeking = { playerViewModel.dispatch(PlayerAction.SeekTo(it)) },
                onClickStop = { playerViewModel.dispatch(PlayerAction.Stop) }
            )
        }
    }
}
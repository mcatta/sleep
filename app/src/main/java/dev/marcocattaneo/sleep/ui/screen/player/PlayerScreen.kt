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

package dev.marcocattaneo.sleep.ui.screen.player

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.marcocattaneo.sleep.R
import dev.marcocattaneo.sleep.domain.model.Minutes
import dev.marcocattaneo.sleep.domain.model.Seconds
import dev.marcocattaneo.sleep.domain.model.sec
import dev.marcocattaneo.sleep.ui.composables.BottomPlayerBar
import dev.marcocattaneo.sleep.ui.composables.Snackbar
import dev.marcocattaneo.sleep.ui.composables.animations.CollapseAnimation
import dev.marcocattaneo.sleep.ui.theme.Dimen.Margin16
import dev.marcocattaneo.sleep.ui.theme.Dimen.Margin8

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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                content = content
            )
            PlayerController(
                modifier = Modifier.Companion.align(Alignment.BottomCenter).padding(bottom = Margin8),
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
    val position: Seconds
    val duration: Seconds
    val isPlaying: Boolean
    val stopTimer: Minutes?
    when (uiState) {
        is PlayerState.Pause,
        is PlayerState.Playing -> (uiState as PlayerState.CommonPlayingState).let {
            position = it.position
            duration = it.duration
            isPlaying = uiState is PlayerState.Playing
            stopTimer = it.stopTimer
        }

        else -> {
            position = 0.sec
            duration = 0.sec
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
                modifier = Modifier.padding(horizontal = Margin16),
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
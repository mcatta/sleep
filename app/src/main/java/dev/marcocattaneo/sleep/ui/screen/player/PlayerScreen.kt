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
import dev.marcocattaneo.sleep.domain.model.Minutes
import dev.marcocattaneo.sleep.domain.model.Seconds
import dev.marcocattaneo.sleep.domain.model.sec
import dev.marcocattaneo.sleep.ui.composables.BottomPlayerBar
import dev.marcocattaneo.sleep.ui.composables.animations.CollapseAnimation
import dev.marcocattaneo.sleep.ui.theme.Dimen.Margin16

@Composable
fun PlayerScreen(
    playerViewModel: PlayerViewModel,
    content: @Composable ColumnScope.() -> Unit
) {
    val uiState by playerViewModel.rememberState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            content = content
        )
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
        CollapseAnimation(
            visible = isVisible,
            modifier = Modifier.align(Alignment.BottomCenter)
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
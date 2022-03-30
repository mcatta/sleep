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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.marcocattaneo.sleep.ui.composables.BottomPlayerBar
import dev.marcocattaneo.sleep.ui.composables.animations.CollapseAnimation
import dev.marcocattaneo.sleep.ui.theme.Dimen.Margin16

@Composable
fun PlayerScreen(
    playerViewModel: PlayerViewModel,
    content: @Composable ColumnScope.() -> Unit
) {
    val uiState by playerViewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            content = content
        )
        CollapseAnimation(
            visible = uiState.playerStatus !in listOf(
                PlayerState.PlayerStatus.Disposed,
                PlayerState.PlayerStatus.Stop,
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = Margin16)
        ) {
            BottomPlayerBar(
                position = uiState.position,
                duration = uiState.duration,
                isPlaying = uiState.playerStatus is PlayerState.PlayerStatus.Playing,
                onChangePlayingStatus = { isPlaying ->
                    if (isPlaying) {
                        playerViewModel.process(PlayerAction.Play)
                    } else {
                        playerViewModel.process(PlayerAction.Pause)
                    }
                },
                onChangeStopTimer = {
                    playerViewModel.process(PlayerAction.StopAfter(it))
                }
            )
        }
    }
}
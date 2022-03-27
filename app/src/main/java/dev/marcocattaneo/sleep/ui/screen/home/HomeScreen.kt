/*
 * Copyright 2021 Marco Cattaneo
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

package dev.marcocattaneo.sleep.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import dev.marcocattaneo.sleep.domain.model.MediaFile
import dev.marcocattaneo.sleep.ui.theme.Dimen
import dev.marcocattaneo.sleep.ui.theme.placeholder

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onClickTrackCallback: (String) -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(homeViewModel) {
        homeViewModel.process(HomeAction.CheckAudioList)
    }

    LazyColumn {
        if (uiState.showLoading) {
            repeat(5) {
                item {
                    MediaItem(
                        modifier = Modifier.placeholder(true),
                        mediaFile = null
                    ) {}
                }
            }
        } else {
            uiState.mediaFiles.forEach {
                item { MediaItem(mediaFile = it, onClick = onClickTrackCallback) }
            }
        }
    }
}

@Composable
private fun MediaItem(
    modifier: Modifier = Modifier,
    mediaFile: MediaFile?,
    onClick: (String) -> Unit
) {
    Text(
        text = mediaFile?.name ?: "Undefined",
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onClick(mediaFile?.path ?: "") },
                role = Role.Button,
                enabled = true
            )
            .padding(
                horizontal = Dimen.Margin16,
                vertical = Dimen.Margin8
            )
            .then(modifier)
    )
}
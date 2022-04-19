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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.marcocattaneo.sleep.R
import dev.marcocattaneo.sleep.domain.model.MediaFile
import dev.marcocattaneo.sleep.ui.composables.*
import dev.marcocattaneo.sleep.ui.theme.Dimen
import dev.marcocattaneo.sleep.ui.theme.placeholder

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onClickMediaFile: (MediaFile) -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // Show the loading on first access
        homeViewModel.process(HomeAction.ShowLoading)
    }
    homeViewModel.process(HomeAction.CheckAudioList)

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(all = Dimen.Margin16)
        ) {
            H4(
                text = stringResource(id = R.string.home_header),
                color = MaterialTheme.colors.onBackground
            )
            Body2(
                text = stringResource(id = R.string.home_header_claim),
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
            )
            Spacer16()
            InfoBox(modifier = Modifier.fillMaxWidth()) {
                Illustration(
                    modifier = Modifier.size(48.dp),
                    resource = R.drawable.ui_undraw_late_at_night
                )
                Spacer16()
                Body2(text = stringResource(id = R.string.home_info_banner))
            }

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
                uiState.homeMediaFile.forEach {
                    item { MediaItem(mediaFile = it, onClick = onClickMediaFile) }
                }
            }
        }
    }
}

@Composable
private fun MediaItem(
    modifier: Modifier = Modifier,
    mediaFile: MediaFile?,
    onClick: (MediaFile) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { mediaFile?.let(onClick) },
                role = Role.Button,
                enabled = true
            )
            .padding(
                horizontal = Dimen.Margin16,
                vertical = Dimen.Margin8
            )
            .then(modifier)
    ) {
        val selected = mediaFile?.selected == true
        Icon(
            painter = painterResource(id = if (selected) R.drawable.ic_baseline_play_circle_filled_24 else R.drawable.ic_baseline_play_circle_outline_24),
            contentDescription = mediaFile?.name ?: "Undefined",
            tint = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
        )
        Spacer8()
        Column {
            Body1(
                text = mediaFile?.name ?: "Undefined",
                color = MaterialTheme.colors.onBackground
            )
            mediaFile?.description?.let { description ->
                Caption(
                    text = description,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f)
                )
            }
        }
    }
}
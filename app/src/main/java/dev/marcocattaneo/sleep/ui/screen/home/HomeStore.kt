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

package dev.marcocattaneo.sleep.ui.screen.home

import arrow.core.Either
import dagger.hilt.android.scopes.ViewModelScoped
import dev.marcocattaneo.mvi.State
import dev.marcocattaneo.mvi.intent.Action
import dev.marcocattaneo.mvi.store.ChannelStore
import dev.marcocattaneo.sleep.di.scope.CoroutineContextScope
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.model.MediaFile
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ViewModelScoped
class HomeStore @Inject constructor(
    @CoroutineContextScope coroutineScope: CoroutineScope
): ChannelStore<HomeState>(
    initialState = HomeState(),
    scope = coroutineScope
)

data class HomeState(
    val showLoading: Boolean = false,
    val mediaFiles: List<MediaFile> = emptyList(),
    val selectedTrackId: String? = null,
    val showError: String? = null
): State {
    val homeMediaFile: List<MediaFile>
        get() = mediaFiles.map { it.copy(selected = it.id == selectedTrackId) }
}

sealed interface HomeAction: Action {
    object ShowLoading: HomeAction
    object CheckAudioList: HomeAction
    data class CheckAudioListResult(val result: Either<AppException, List<MediaFile>>): HomeAction

    data class UpdateTrack(val trackId: String? = null): HomeAction
}
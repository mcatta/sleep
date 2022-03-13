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

package dev.marcocattaneo.asmrelax.ui.screen.login

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.marcocattaneo.asmrelax.domain.model.MediaFile
import dev.marcocattaneo.asmrelax.domain.repository.MediaRepository
import dev.marcocattaneo.asmrelax.navigation.routing.generatePath
import dev.marcocattaneo.asmrelax.ui.screen.RouteKeys
import dev.marcocattaneo.asmrelax.ui.screen.Routes
import dev.marcocattaneo.asmrelax.ui.screen.common.StateViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : StateViewModel<HomeViewModel.Status>(
    initialState = Status()
) {

    fun fetch(): Job {
        val launch = viewModelScope.launch {
            when (val res = mediaRepository.listMedia()) {
                is Either.Left -> {
                    // TODO error
                }
                is Either.Right -> {
                    emitState { state ->
                        state.copy(
                            mediaFiles = res.value
                        )
                    }
                }
            }
        }
        return launch
    }

    fun openPlayer(url: String) = viewModelScope.launch {
        navigateTo(Routes.Player.generatePath(RouteKeys.PATH_KEY to url))
    }

    data class Status(
        val mediaFiles: List<MediaFile> = emptyList(),
        val showError: String? = null
    )
}
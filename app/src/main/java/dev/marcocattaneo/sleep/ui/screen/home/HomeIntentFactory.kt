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
import dev.marcocattaneo.sleep.domain.repository.MediaRepository
import dev.marcocattaneo.mvi.intent.Intent
import dev.marcocattaneo.mvi.intent.IntentFactory
import dev.marcocattaneo.mvi.intent.intent
import dev.marcocattaneo.mvi.intent.sideEffect
import javax.inject.Inject

class HomeIntentFactory @Inject constructor(
    private val mediaRepository: MediaRepository,
    homeStore: HomeStore
) : IntentFactory<HomeState, HomeAction>(
    store = homeStore
) {

    override suspend fun buildIntent(action: HomeAction): Intent<HomeState> = when (action) {

        HomeAction.ShowLoading -> intent { copy(showLoading = true) }

        HomeAction.CheckAudioList -> sideEffect {
            HomeAction.CheckAudioListResult(mediaRepository.listMedia())
        }

        is HomeAction.CheckAudioListResult -> intent {
            when (action.result) {
                is Either.Left -> copy(
                    showLoading = false,
                    showError = action.result.value.localizedMessage
                )
                is Either.Right -> copy(
                    showLoading = false,
                    mediaFiles = action.result.value
                )
            }
        }

        is HomeAction.UpdateTrack -> intent {
            copy(selectedTrackId = action.trackId)
        }
    }
}
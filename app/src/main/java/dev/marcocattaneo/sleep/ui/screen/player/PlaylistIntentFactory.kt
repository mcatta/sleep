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

import dev.marcocattaneo.mvi.intent.Intent
import dev.marcocattaneo.mvi.intent.IntentFactory
import dev.marcocattaneo.mvi.intent.intent
import javax.inject.Inject

class PlaylistIntentFactory @Inject constructor(
    playlistStore: PlaylistStore
) : IntentFactory<PlaylistState, PlaylistAction>(
    store = playlistStore
) {
    override suspend fun buildIntent(action: PlaylistAction): Intent<PlaylistState> = when (action) {
        PlaylistAction.Clear -> intent { copy(currentTrackId = null) }
        is PlaylistAction.Update -> intent { copy(currentTrackId = action.trackId) }
    }
}
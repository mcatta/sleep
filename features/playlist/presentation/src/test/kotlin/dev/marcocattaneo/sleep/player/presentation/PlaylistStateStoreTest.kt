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

package dev.marcocattaneo.sleep.player.presentation

import app.cash.turbine.test
import dev.marcocattaneo.sleep.playlist.presentation.PlaylistAction
import dev.marcocattaneo.sleep.playlist.presentation.PlaylistStateStore
import io.mockk.MockKAnnotations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class PlaylistStateStoreTest {

    private lateinit var playlistStateStore: PlaylistStateStore

    private lateinit var testCoroutineScope: CoroutineScope

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)
        testCoroutineScope = CoroutineScope(Dispatchers.Unconfined)
        playlistStateStore =
            PlaylistStateStore(testCoroutineScope)
    }

    @Test
    fun `Test PlaylistAction PlaylistAction`() = runTest {
        playlistStateStore.stateFlow.test {
            // When
            playlistStateStore.dispatchAction(PlaylistAction.Update("trackId"))

            // Then
            assertNull(awaitItem().currentTrackId)
            assertEquals("trackId", awaitItem().currentTrackId)
        }
    }

    @Test
    fun `Test PlaylistAction Clear`() = runTest {
        playlistStateStore.stateFlow.test {
            // Given
            playlistStateStore.dispatchAction(PlaylistAction.Update("trackId"))
            playlistStateStore.dispatchAction(PlaylistAction.Clear)

            // Then
            assertNull(awaitItem().currentTrackId)
            assertEquals("trackId", awaitItem().currentTrackId)
            assertNull(awaitItem().currentTrackId)
        }
    }

}
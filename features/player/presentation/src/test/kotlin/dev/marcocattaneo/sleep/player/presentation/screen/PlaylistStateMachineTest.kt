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

package dev.marcocattaneo.sleep.player.presentation.screen

import app.cash.turbine.test
import dev.marcocattaneo.sleep.player.presentation.screen.PlaylistAction
import dev.marcocattaneo.sleep.player.presentation.screen.PlaylistStateMachine
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class PlaylistStateMachineTest {

    private lateinit var playlistStateMachine: PlaylistStateMachine

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)
        playlistStateMachine = PlaylistStateMachine()
    }

    @Test
    fun `Test PlaylistAction PlaylistAction`() = runTest {
        playlistStateMachine.state.test {
            // When
            playlistStateMachine.dispatch(PlaylistAction.Update("trackId"))

            // Then
            assertNull(awaitItem().currentTrackId)
            assertEquals("trackId", awaitItem().currentTrackId)
        }
    }

    @Test
    fun `Test PlaylistAction Clear`() = runTest {
        playlistStateMachine.state.test {
            // Given
            playlistStateMachine.dispatch(PlaylistAction.Update("trackId"))
            playlistStateMachine.dispatch(PlaylistAction.Clear)

            // Then
            assertNull(awaitItem().currentTrackId)
            assertEquals("trackId", awaitItem().currentTrackId)
            assertNull(awaitItem().currentTrackId)
        }
    }
}
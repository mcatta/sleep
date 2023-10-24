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

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import dev.marcocattaneo.sleep.playlist.presentation.PlaylistEvent
import dev.marcocattaneo.sleep.playlist.presentation.PlaylistPresenter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class PlaylistPresenterTest {

    private companion object {
        const val FAKE_TRACK_ID = "7637922b-19ea-405e-a312-83d3fc438abd"
    }

    private val presenter = PlaylistPresenter()

    @Test
    fun `Test clear playlist`() = runTest {
        // Given
        val events = flowOf(PlaylistEvent.Clear)

        // When / Then
        moleculeFlow(RecompositionMode.Immediate) { presenter.models(events) }.test {
            assertNull(awaitItem().currentTrackId)
        }
    }

    @Test
    fun `Test update track on the playlist`() = runTest {
        // Given
        val events = flowOf(PlaylistEvent.Update(FAKE_TRACK_ID))

        // When / Then
        moleculeFlow(RecompositionMode.Immediate) { presenter.models(events) }.test {
            assertNull(awaitItem().currentTrackId)
            assertEquals(FAKE_TRACK_ID, awaitItem().currentTrackId)
        }
    }

}
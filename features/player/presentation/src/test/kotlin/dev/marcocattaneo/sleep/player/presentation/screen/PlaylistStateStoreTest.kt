package dev.marcocattaneo.sleep.player.presentation.screen

import app.cash.turbine.test
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
        playlistStateStore = PlaylistStateStore(testCoroutineScope)
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
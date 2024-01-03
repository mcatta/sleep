package dev.marcocattaneo.sleep.player.data.repository

import arrow.core.Either
import dev.marcocattaneo.sleep.data.auth.AuthDataSource
import dev.marcocattaneo.sleep.data.http.SleepService
import dev.marcocattaneo.sleep.data.model.MediaUrl
import dev.marcocattaneo.sleep.data.repository.BaseRepositoryImpl
import dev.marcocattaneo.sleep.player.domain.repository.PlayerRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import java.net.SocketException
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class PlayerRepositoryImplTest {

    private lateinit var playerRepository: PlayerRepository

    @MockK
    lateinit var sleepService: SleepService

    @MockK
    lateinit var authDataSource: AuthDataSource

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)

        coEvery { authDataSource.getAuthToken() } returns Either.Right("token")

        playerRepository = PlayerRepositoryImpl(
            authDataSource = authDataSource,
            sleepService = sleepService,
            baseRepository = BaseRepositoryImpl()
        )
    }

    @Test
    fun `Test urlFromPath upon a success`() = runTest {
        // Given
        coEvery { sleepService.downloadUrl(any(), any()) } returns  MediaUrl("url", "path")

        // When
        val res = playerRepository.urlFromId("path")

        // Then
        kotlin.test.assertEquals(true, res.isRight())
        coVerify { sleepService.downloadUrl(any(), eq("path")) }
    }

    @Test
    fun `Test urlFromPath upon a failure`() = runTest {
        // Given
        coEvery { sleepService.downloadUrl(any(), any()) } throws SocketException("Failure")

        // When
        val res = playerRepository.urlFromId("path")

        // Then
        kotlin.test.assertEquals(true, res.isLeft())
        coVerify { sleepService.downloadUrl(any(), eq("path")) }
    }

}
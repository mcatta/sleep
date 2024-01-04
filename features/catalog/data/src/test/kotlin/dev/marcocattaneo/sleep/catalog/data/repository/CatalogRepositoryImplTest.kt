package dev.marcocattaneo.sleep.catalog.data.repository

import arrow.core.Either
import dev.marcocattaneo.sleep.catalog.data.mapper.MediaFileMapper
import dev.marcocattaneo.sleep.catalog.data.mock
import dev.marcocattaneo.sleep.catalog.domain.model.MediaFileEntity
import dev.marcocattaneo.sleep.catalog.domain.repository.CatalogRepository
import dev.marcocattaneo.sleep.data.auth.AuthDataSource
import dev.marcocattaneo.sleep.data.http.SleepService
import dev.marcocattaneo.sleep.data.model.MediaFile
import dev.marcocattaneo.sleep.data.repository.BaseRepositoryImpl
import dev.marcocattaneo.sleep.domain.AppException
import dev.marcocattaneo.sleep.domain.cache.CachePolicy
import dev.marcocattaneo.sleep.domain.cache.CacheService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import java.net.SocketException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CatalogRepositoryImplTest {

    private companion object {
        const val FAKE_TOKEN = "82a869d0-67a9-4163-a099-048728e08d1c"
    }

    private lateinit var catalogRepository: CatalogRepository

    @MockK
    lateinit var sleepService: SleepService

    @MockK
    lateinit var mediaFileCache: CacheService<String, List<MediaFileEntity>>

    @MockK
    lateinit var authDataSource: AuthDataSource

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)

        coEvery { mediaFileCache.getValue(any(), any<CachePolicy>()) } returns null
        coEvery { mediaFileCache.setValue(any(), any(), any()) } just Runs
        coEvery { authDataSource.getAuthToken() } returns Either.Right(FAKE_TOKEN)

        catalogRepository = CatalogRepositoryImpl(
            mediaFileMapper = MediaFileMapper(),
            mediaFileCache = mediaFileCache,
            authDataSource = authDataSource,
            sleepService = sleepService,
            baseRepository = BaseRepositoryImpl()
        )
    }

    @Test
    fun `Test API without token`() = runTest {
        // Given
        coEvery { authDataSource.getAuthToken() } returns Either.Left(AppException.GenericError)
        coEvery { sleepService.tracks(any()) } returns emptyList()

        // When
        val res = catalogRepository.listMedia()

        // Then
        assertEquals(true, res.isLeft())
        coVerify(exactly = 0) { sleepService.tracks(any()) }
    }

    @Test
    fun `Test listMedia upon a success`() = runTest {
        // Given
        coEvery { sleepService.tracks(any()) } returns listOf(
            MediaFile.mock(id = "1"), MediaFile.mock(id = "2")
        )

        // When
        val res = catalogRepository.listMedia()

        // Then
        assertEquals(true, res.isRight())
        assertEquals(2, (res as Either.Right).value.size)
        coVerify { sleepService.tracks(eq("Bearer $FAKE_TOKEN")) }
    }

    @Test
    fun `Test listMedia upon a failure`() = runTest {
        // Given
        coEvery { sleepService.tracks(any()) } throws SocketException("Failure")

        // When
        val res = catalogRepository.listMedia()

        // Then
        assertEquals(true, res.isLeft())
        coVerify { sleepService.tracks(eq("Bearer $FAKE_TOKEN")) }
    }

}
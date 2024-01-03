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
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.test.runTest
import java.net.SocketException
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class CatalogRepositoryImplTest {

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
        coEvery { authDataSource.getAuthToken() } returns Either.Right("token")

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
        kotlin.test.assertEquals(true, res.isLeft())
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
        kotlin.test.assertEquals(true, res.isRight())
        kotlin.test.assertEquals(2, (res as Either.Right).value.size)
    }

    @Test
    fun `Test listMedia upon a failure`() = runTest {
        // Given
        coEvery { sleepService.tracks(any()) } throws SocketException("Failure")

        // When
        val res = catalogRepository.listMedia()

        // Then
        kotlin.test.assertEquals(true, res.isLeft())
    }

}
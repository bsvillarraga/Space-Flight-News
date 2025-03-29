package com.bsvillarraga.spaceflightnews.data.repository

import android.accounts.NetworkErrorException
import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.data.FakeData.fakePaginationWithLaunchesAndEvents
import com.bsvillarraga.spaceflightnews.data.FakeData.fakePaginationWithoutArticles
import com.bsvillarraga.spaceflightnews.data.model.PaginationDto
import com.bsvillarraga.spaceflightnews.data.remote.ApiHelper
import com.bsvillarraga.spaceflightnews.data.remote.ApiResponse
import com.bsvillarraga.spaceflightnews.data.remote.articles.ArticlesApiClient
import com.bsvillarraga.spaceflightnews.data.utils.PaginationManager
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException

class ArticleRepositoryImplTest {
    @MockK
    private lateinit var api: ArticlesApiClient

    @MockK
    private lateinit var apiHelper: ApiHelper

    @MockK
    private lateinit var paginationManager: PaginationManager

    private lateinit var repository: ArticleRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        repository = ArticleRepositoryImpl(api, apiHelper, paginationManager)
    }

    @Test
    fun `When API return success without query, then return pagination with articles`() =
        runTest {
            val data = Response.success(
                fakePaginationWithLaunchesAndEvents
            )

            coEvery { api.getArticles(any(), any(), any()) } returns data
            coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.Companion.create(
                data
            )

            val result = repository.getArticles()

            coVerify(exactly = 1) { apiHelper.safeApiCall<PaginationDto>(any()) }

            assertTrue(result is Resource.Success)

            assertEquals(
                fakePaginationWithLaunchesAndEvents,
                (result as Resource.Success).data
            )
        }

    @Test
    fun `When API return success with query, then return pagination with articles`() =
        runTest {
            val data = Response.success(
                fakePaginationWithLaunchesAndEvents
            )

            coEvery { api.getArticles(any(), any(), any()) } returns data
            coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.create(data)

            val result = repository.getArticles("NASA")

            coVerify(exactly = 1) { apiHelper.safeApiCall<PaginationDto>(any()) }

            assertTrue(result is Resource.Success)

            assertEquals(
                fakePaginationWithLaunchesAndEvents,
                (result as Resource.Success).data
            )
        }

    @Test
    fun `When API return success with query, then return pagination without articles`() =
        runTest {
            val data = Response.success(
                fakePaginationWithoutArticles
            )

            coEvery { api.getArticles(any(), any(), any()) } returns data
            coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.create(data)

            val result = repository.getArticles("ASDF ASDF")

            coVerify(exactly = 1) { apiHelper.safeApiCall<PaginationDto>(any()) }

            assertTrue(result is Resource.Success)

            assertEquals(
                fakePaginationWithoutArticles,
                (result as Resource.Success).data
            )
        }

    @Test
    fun `When API return timeout, then return exception`() = runTest {
        // Simulamos un timeout
        val timeOutException = SocketTimeoutException("Timeout")
        coEvery { api.getArticles(limit = any()) } throws timeOutException
        coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.create(
            timeOutException
        )

        // Ejecutamos el llamado al repositorio
        val result = repository.getArticles()

        // Verificamos que se haga solo un llamado al servicio
        coVerify(exactly = 1) { apiHelper.safeApiCall<PaginationDto>(any()) }

        // Verificamos que el resultado sea un ApiErrorResponse
        assertTrue(result is Resource.Error)
        assertEquals("Timeout", (result as Resource.Error).msg)
        assertTrue(result.error is SocketTimeoutException)
    }

    @Test
    fun `When API return code 524, then the repository handles the error correctly`() = runTest {
        val errorResponse = Response.error<PaginationDto>(
            524,
            "Gateway Timeout".toResponseBody("application/json".toMediaTypeOrNull())
        )

        coEvery { api.getArticles(limit = any()) } throws HttpException(errorResponse)
        coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.create(
            errorResponse
        )

        val result = repository.getArticles()

        assertTrue(result is Resource.Error)
        assertEquals("524", (result as Resource.Error).code)
        assertEquals("Gateway Timeout", result.msg)
    }

    @Test
    fun `When API fails due to network error, then return connection error`() = runTest {
        val networkException = NetworkErrorException("Error de conexión")
        coEvery { api.getArticles(limit = any()) } throws networkException
        coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.create(
            networkException
        )

        val result = repository.getArticles()

        assertTrue(result is Resource.Error)
        assertEquals("2", (result as Resource.Error).code)
        assertEquals("Error de conexión", result.msg)
        assertTrue(result.error is NetworkErrorException)
    }

    @Test
    fun `When API fails due to unknown error, then return generic error`() = runTest {

    }

    /*@Test
    fun `cuando API retorna 200 pero sin datos, el repositorio debe devolver lista vacía`() = runTest {
        // Simulamos respuesta del API con una lista vacía
        coEvery { api.getArticles(any(), any(), any()) } returns Response.success(ArticleDto(emptyList()))

        // Ejecutamos el método del repositorio
        val result = repository.getArticles(null, null, 10).first()

        // Validamos que sea un éxito pero sin datos
        assertTrue(result is Resource.Success)
        assertTrue(result.data?.articles?.isEmpty() == true)
    }*/
}
package com.bsvillarraga.spaceflightnews.data.repository

import android.accounts.NetworkErrorException
import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.rules.CoroutinesTestRule
import com.bsvillarraga.spaceflightnews.utils.FakeData.fakeArticle
import com.bsvillarraga.spaceflightnews.utils.FakeData.fakePaginationWithLaunchesAndEvents
import com.bsvillarraga.spaceflightnews.utils.FakeData.fakePaginationWithoutArticles
import com.bsvillarraga.spaceflightnews.data.model.ArticleDto
import com.bsvillarraga.spaceflightnews.data.model.PaginationDto
import com.bsvillarraga.spaceflightnews.data.remote.ApiHelper
import com.bsvillarraga.spaceflightnews.data.remote.ApiResponse
import com.bsvillarraga.spaceflightnews.data.remote.articles.ArticlesApiClient
import com.bsvillarraga.spaceflightnews.data.utils.PaginationManager
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Rule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleRepositoryImplTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

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
        coroutinesTestRule.testScope.runTest {
            testGetArticlesSuccess(fakeData = fakePaginationWithLaunchesAndEvents)
        }

    @Test
    fun `When API return success with query, then return pagination with articles`() =
        coroutinesTestRule.testScope.runTest {
            testGetArticlesSuccess("NASA", fakePaginationWithLaunchesAndEvents)
        }

    @Test
    fun `When API return success with query, then return pagination without articles`() =
        coroutinesTestRule.testScope.runTest {
            testGetArticlesSuccess("Sin Datos", fakePaginationWithoutArticles)
        }

    private suspend fun testGetArticlesSuccess(query: String? = null, fakeData: PaginationDto) {
        // Simulación de datos de una respuesta exitosa
        val data = Response.success(fakeData)

        // Configuración de respuestas para los mocks
        coEvery { paginationManager.getCurrentOffset() } returns null //No hay paginación previa
        coEvery { api.getArticles() } returns data
        coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.create(
            data
        )
        coEvery { paginationManager.updatePagination(any()) } just Runs

        // Ejecutamos getArticles, para obtener la data
        val result = repository.getArticles(query)

        //Verificamos que apiHelper, se llame solo una vez
        coVerify(exactly = 1) { apiHelper.safeApiCall<PaginationDto>(any()) }

        //Verficiamos que paginationManager, se llame solo una vez
        coVerify(exactly = 1) { paginationManager.updatePagination(fakeData) }

        //Verificamos que el resultado sea un Resource.Success
        assertTrue(result is Resource.Success)

        //Comparamos los datos esperados con los obtenidos
        assertEquals(
            fakeData.articles.map { it.toArticleDomain() },
            (result as Resource.Success).data
        )
    }

    @Test
    fun `When API return timeout, then return exception`() = runTest {
        // Simulamos un timeout
        val timeOutException = SocketTimeoutException("Timeout")

        // Configuración de respuestas para los mocks
        coEvery { api.getArticles() } throws timeOutException
        coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.create(
            timeOutException
        )

        // Ejecutamos getArticles, para obtener la data
        val result = repository.getArticles()

        //Verificamos que apiHelper, se llame solo una vez
        coVerify(exactly = 1) { apiHelper.safeApiCall<PaginationDto>(any()) }

        //Verificamos que el resultado sea un Resource.Error
        assertTrue(result is Resource.Error)

        //Verificamos que el mensaje sea el esperado
        assertEquals("Timeout", (result as Resource.Error).msg)

        //Verificamos que el error sea un SocketTimeoutException
        assertTrue(result.error is SocketTimeoutException)
    }

    @Test
    fun `When API return code 524, then the repository handles the error correctly`() = runTest {
        //Simulamos una respuesta con código 524
        val errorResponse = Response.error<PaginationDto>(
            524,
            "Gateway Timeout".toResponseBody("application/json".toMediaTypeOrNull())
        )

        // Configuración de respuestas para los mocks
        coEvery { api.getArticles() } throws HttpException(errorResponse)
        coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.create(
            errorResponse
        )

        // Ejecutamos getArticles, para obtener la data
        val result = repository.getArticles()

        //Verificamos que apiHelper, se llame solo una vez
        coVerify(exactly = 1) { apiHelper.safeApiCall<PaginationDto>(any()) }

        // Verificamos que el resultado sea un Resource.Error
        assertTrue(result is Resource.Error)

        // Verificamos que el código sea el esperado
        assertEquals("524", (result as Resource.Error).code)

        // Verificamos que el mensaje sea el esperado
        assertEquals("Gateway Timeout", result.msg)
    }

    @Test
    fun `When API fails due to network error, then return connection error`() = runTest {
        // Simulamos una excepción de red
        val networkException = NetworkErrorException("Error de conexión")

        // Configuración de respuestas para los mocks
        coEvery { api.getArticles(limit = any()) } throws networkException
        coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.create(
            networkException
        )

        // Ejecutamos getArticles, para obtener la data
        val result = repository.getArticles()

        //Verificamos que apiHelper, se llame solo una vez
        coVerify(exactly = 1) { apiHelper.safeApiCall<PaginationDto>(any()) }

        // Verificamos que el resultado sea un Resource.Error
        assertTrue(result is Resource.Error)

        // Verificamos que el código sea el esperado
        assertEquals("2", (result as Resource.Error).code)

        // Verificamos que el mensaje sea el esperado
        assertEquals("Error de conexión", result.msg)

        // Verificamos que el error sea un NetworkErrorException
        assertTrue(result.error is NetworkErrorException)
    }

    @Test
    fun `When API fails due to unknown error, then return generic error`() = runTest {
        // Simulamos una excepción de red
        val connectException = ConnectException("Error desconocido")

        // Configuración de respuestas para los mocks
        coEvery { api.getArticles(limit = any()) } throws connectException
        coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.create(
            connectException
        )

        // Ejecutamos getArticles, para obtener la data
        val result = repository.getArticles()

        //Verificamos que apiHelper, se llame solo una vez
        coVerify(exactly = 1) { apiHelper.safeApiCall<PaginationDto>(any()) }

        // Verificamos que el resultado sea un Resource.Error
        assertTrue(result is Resource.Error)

        // Verificamos que el código sea el esperado
        assertEquals("4", (result as Resource.Error).code)

        // Verificamos que el mensaje sea el esperado
        assertEquals("Error desconocido", result.msg)

        // Verificamos que el error sea un NetworkErrorException
        assertTrue(result.error is ConnectException)
    }

    @Test
    fun `When API returns null body, then return error`() = runTest {
        // Simulamos una excepción de red
        val nullResponse = Response.success<PaginationDto?>(null)

        // Configuración de respuestas para los mocks
        coEvery { api.getArticles(limit = any()) } returns nullResponse
        coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.create(
            nullResponse
        )

        // Ejecutamos getArticles, para obtener la data
        val result = repository.getArticles()

        //Verificamos que apiHelper, se llame solo una vez
        coVerify(exactly = 1) { apiHelper.safeApiCall<PaginationDto>(any()) }

        // Verificamos que el resultado sea un Resource.Error
        assertTrue(result is Resource.Error)

        // Verificamos que el código sea el esperado
        assertEquals("1", (result as Resource.Error).code)

        // Verificamos que el mensaje sea el esperado
        assertEquals("La respuesta del body es null", result.msg)
    }

    @Test
    fun `When searching for an article by ID and the response is successful`() =
        coroutinesTestRule.testScope.runTest {
            // Simulación de datos de una respuesta exitosa
            val fakeData = Response.success(fakeArticle)

            coEvery { api.getArticleById(any()) } returns fakeData
            coEvery { apiHelper.safeApiCall<ArticleDto>(any()) } returns ApiResponse.create(
                fakeData
            )

            // Ejecutamos getArticles, para obtener la data
            val result = repository.getArticleById(articleId = 2064)

            //Verificamos que apiHelper, se llame solo una vez
            coVerify(exactly = 1) { apiHelper.safeApiCall<ArticleDto>(any()) }

            //Verificamos que el resultado sea un Resource.Success
            assertTrue(result is Resource.Success)

            //Comparamos los datos esperados con los obtenidos
            assertEquals(
                fakeArticle.toArticleDetailDomain(),
                (result as Resource.Success).data
            )
        }

    @Test
    fun `When searching for an article by ID and it was not found`() =
        coroutinesTestRule.testScope.runTest {
            //Simulamos una respuesta con código 524
            val errorResponse = Response.error<PaginationDto>(
                404,
                "Not Found".toResponseBody("application/json".toMediaTypeOrNull())
            )
            // Configuración de respuestas para los mocks
            coEvery { api.getArticleById(any()) } throws HttpException(errorResponse)
            coEvery { apiHelper.safeApiCall<PaginationDto>(any()) } returns ApiResponse.create(
                errorResponse
            )

            // Ejecutamos getArticles, para obtener la data
            val result = repository.getArticleById(articleId = -1)

            //Verificamos que apiHelper, se llame solo una vez
            coVerify(exactly = 1) { apiHelper.safeApiCall<PaginationDto>(any()) }

            // Verificamos que el resultado sea un Resource.Error
            assertTrue(result is Resource.Error)

            // Verificamos que el código sea el esperado
            assertEquals("404", (result as Resource.Error).code)

            // Verificamos que el mensaje sea el esperado
            assertEquals("Not Found", result.msg)
        }
}
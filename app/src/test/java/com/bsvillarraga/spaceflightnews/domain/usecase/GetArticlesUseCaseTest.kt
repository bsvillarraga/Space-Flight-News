package com.bsvillarraga.spaceflightnews.domain.usecase

import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.repository.ArticleRepository
import com.bsvillarraga.spaceflightnews.utils.FakeData.fakeArticleList
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class GetArticlesUseCaseTest {
    @MockK
    private lateinit var repository: ArticleRepository

    private lateinit var getArticlesUseCase: GetArticlesUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        getArticlesUseCase = GetArticlesUseCase(repository)
    }

    @Test
    fun `When getArticles is called, it should return success`() =
        runTest {
            // Mockeamos la respuesta del repositorio
            coEvery { repository.getArticles(any()) } returns Resource.Success(fakeArticleList)

            // Llamamos al caso de uso
            val result = getArticlesUseCase.getArticles()

            // Verificamos que getArticles, se llame solo una vez
            coVerify(exactly = 1) { repository.getArticles() }

            // Verificamos que el resultado sea un Resource.Success
            assertTrue(result is Resource.Success)

            // Validamos los datos
            assertEquals(fakeArticleList, (result as Resource.Success).data)
        }

    @Test
    fun `When getArticles is called and has a query, it should return success`() =
        runTest {
            // Mockeamos la respuesta del repositorio
            coEvery { repository.getArticles(any()) } returns Resource.Success(fakeArticleList)

            // Llamamos al caso de uso
            val result = getArticlesUseCase.getArticles(query = "NASA")

            // Verificamos que getArticles, se llame solo una vez
            coVerify(exactly = 1) { repository.getArticles(any()) }

            // Verificamos que el resultado sea un Resource.Success
            assertTrue(result is Resource.Success)

            // Validamos los datos
            assertEquals(fakeArticleList, (result as Resource.Success).data)
        }

    @Test
    fun `When getArticles fails, it should return error`() =
        runTest {
            // Mockeamos la respuesta del repositorio
            coEvery { repository.getArticles(any()) } returns Resource.Error(null, "Algún error")

            // Llamamos al caso de uso
            val result = getArticlesUseCase.getArticles()

            // Verificamos que getArticles, se llame solo una vez
            coVerify(exactly = 1) { repository.getArticles() }

            // Verificamos que el resultado sea un Resource.Error
            assertTrue(result is Resource.Error)

            // Validamos los datos
            assertEquals("Algún error", (result as Resource.Error).msg)
        }
}
package com.bsvillarraga.spaceflightnews.domain.usecase

import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.repository.ArticleRepository
import com.bsvillarraga.spaceflightnews.utils.FakeData.fakeArticleDetail
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetArticleByIdUseCaseTest {
    @MockK
    private lateinit var repository: ArticleRepository

    private lateinit var getArticleByIdUseCase: GetArticleByIdUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        getArticleByIdUseCase = GetArticleByIdUseCase(repository)
    }

    @Test
    fun `When getArticleById is called, it should return success`() =
        runTest {
            // Mockeamos la respuesta del repositorio
            coEvery { repository.getArticleById(any()) } returns Resource.Success(fakeArticleDetail)

            // Llamamos al caso de uso
            val result = getArticleByIdUseCase.getArticleById(articleId = 20254)

            // Verificamos que getArticles, se llame solo una vez
            coVerify(exactly = 1) { repository.getArticleById(any()) }

            // Verificamos que el resultado sea un Resource.Success
            assertTrue(result is Resource.Success)

            // Validamos los datos
            assertEquals(fakeArticleDetail, (result as Resource.Success).data)
        }

    @Test
    fun `When getArticleById fails, it should return error`() =
        runTest {
            // Mockeamos la respuesta del repositorio
            coEvery { repository.getArticleById(any()) } returns Resource.Error("25", "Algún error")

            // Llamamos al caso de uso
            val result = getArticleByIdUseCase.getArticleById(articleId = -1)

            // Verificamos que getArticles, se llame solo una vez
            coVerify(exactly = 1) { repository.getArticleById(any()) }

            // Verificamos el orden de emisión
            assertTrue(result is Resource.Error)

            // Validamos los datos
            assertEquals("25", (result as Resource.Error).code)
            assertEquals("Algún error", result.msg)
        }
}
package com.bsvillarraga.spaceflightnews.presentation.ui.articles.viewmodel

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.model.ArticleDetail
import com.bsvillarraga.spaceflightnews.domain.usecase.GetArticleByIdUseCase
import com.bsvillarraga.spaceflightnews.utils.FakeData.fakeArticleDetail
import com.bsvillarraga.spaceflightnews.utils.testObserver
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailsViewModelTest {
    @MockK
    private lateinit var articleByIdUseCase: GetArticleByIdUseCase

    private lateinit var viewModel: ArticleDetailsViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(StandardTestDispatcher())

        // Simulamos el comportamiento del Main Thread para LiveData
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
            override fun postToMainThread(runnable: Runnable) = runnable.run()
            override fun isMainThread(): Boolean = true
        })

        viewModel = ArticleDetailsViewModel(articleByIdUseCase)
    }


    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        ArchTaskExecutor.getInstance().setDelegate(null) // Restablecer estado original
    }

    @Test
    fun `When fetchArticleById is called, it should emit Loading then Success`() = runTest {
        val articleId = 1L
        val expectedResult = Resource.Success(fakeArticleDetail)

        coEvery { articleByIdUseCase.getArticleById(articleId) } returns expectedResult

        //Observamos el LiveData
        val observer = viewModel.article.testObserver()

        viewModel.fetchArticleById(id = articleId)

        // Avanza la ejecución de las coroutines
        advanceUntilIdle()

        // Verificamos que el LiveData devuelva Resource.Loading
        assertTrue(observer.values[0] is Resource.Loading)

        // Verificamos que el LiveData devuelva Resource.Success
        assertTrue(observer.values[1] is Resource.Success)

        // Validamos los datos
        assertEquals(fakeArticleDetail, (observer.values[1] as Resource.Success).data)
    }

    @Test
    fun `When fetchArticleById fails, it should emit Loading then Error`() = runTest {
        val articleId = 1L
        val expectedError = Resource.Error<ArticleDetail>("005", "Algún error")

        // Simular un error en la respuesta del caso de uso
        coEvery { articleByIdUseCase.getArticleById(articleId) } returns expectedError

        val observer = viewModel.article.testObserver()

        viewModel.fetchArticleById(id = articleId)

        // Avanza la ejecución de las coroutines
        advanceUntilIdle()

        // Verificamos que el LiveData devuelva Resource.Loading
        assertTrue(observer.values[0] is Resource.Loading)

        // Verificamos que el LiveData devuelva Resource.Error
        assertTrue(observer.values[1] is Resource.Error)

        // Validamos los datos
        assertEquals(expectedError.code, (observer.values[1] as Resource.Error).code)
        assertEquals(expectedError.msg, (observer.values[1] as Resource.Error).msg)
    }

    @Test
    fun `When fetchArticleById is called again without reload, it should not fetch data again`() = runTest {
        val articleId = 1L
        val fakeArticle = Resource.Success(fakeArticleDetail)

        // Simular respuesta del caso de uso
        coEvery { articleByIdUseCase.getArticleById(articleId) } returns fakeArticle

        // Primera llamada
        viewModel.fetchArticleById(articleId)

        // Avanza la ejecución de las coroutines
        advanceUntilIdle()

        // Segunda llamada sin `reload = true`
        viewModel.fetchArticleById(articleId)

        // Verificamos que el caso de uso se llamó solo una vez
        coVerify(exactly = 1) { articleByIdUseCase.getArticleById(articleId) }
    }

    @Test
    fun `When fetchArticleById is called with reload, it should fetch data again`() = runTest {
        val articleId = 1L
        val fakeArticle = Resource.Success(fakeArticleDetail)

        // Simular respuesta del caso de uso
        coEvery { articleByIdUseCase.getArticleById(articleId) } returns fakeArticle

        // Primera llamada
        viewModel.fetchArticleById(articleId)

        // Avanza la ejecución de las coroutines
        advanceUntilIdle()

        // Segunda llamada con `reload = true`
        viewModel.fetchArticleById(articleId, reload = true)

        // Avanza la ejecución de las coroutines
        advanceUntilIdle()

        // Verificamos que el caso de uso se llamó dos veces
        coVerify(exactly = 2) { articleByIdUseCase.getArticleById(articleId) }
    }
}
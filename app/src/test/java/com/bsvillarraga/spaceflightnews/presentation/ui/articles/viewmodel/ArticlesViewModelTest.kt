package com.bsvillarraga.spaceflightnews.presentation.ui.articles.viewmodel

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.usecase.GetArticlesUseCase
import com.bsvillarraga.spaceflightnews.utils.FakeData.fakeArticleList
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
class ArticlesViewModelTest {
    @MockK
    private lateinit var articleUseCase: GetArticlesUseCase

    private lateinit var viewModel: ArticlesViewModel

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

        viewModel = ArticlesViewModel(articleUseCase)
    }


    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        ArchTaskExecutor.getInstance().setDelegate(null) // Restablecer estado original
    }

    @Test
    fun `When fetchArticles is called, it should return success `() = runTest {
        // Simulamos una respuesta exitosa del caso de uso
        coEvery { articleUseCase.getArticles(any()) } returns Resource.Success(fakeArticleList)

        //Observamos el LiveData
        val observer = viewModel.articles.testObserver()

        // Ejecutamos la función a probar
        viewModel.fetchArticles()

        // Avanza la ejecución de las coroutines
        advanceUntilIdle()

        // Verificamos que el LiveData devuelva Resource.Loading
        assertTrue(observer.values[0] is Resource.Loading)

        // Verificamos que el LiveData devuelva Resource.Success
        assertTrue(observer.values[1] is Resource.Success)

        // Validamos los datos
        assertEquals(fakeArticleList, (observer.values[1] as Resource.Success).data)
    }

    @Test
    fun `When fetchArticles is called, it should return error`() = runTest {
        // Simulamos una respuesta de error del caso de uso
        coEvery { articleUseCase.getArticles(any()) } returns Resource.Error("005", "Algún error")

        //Observamos el LiveData
        val observer = viewModel.articles.testObserver()

        // Ejecutamos la función a probar
        viewModel.fetchArticles()

        // Avanza la ejecución de las coroutines
        advanceUntilIdle()

        // Verificamos que el LiveData devuelva Resource.Loading
        assertTrue(observer.values[0] is Resource.Loading)

        // Verificamos que el LiveData devuelva Resource.Error
        assertTrue(observer.values[1] is Resource.Error)

        // Validamos el código de error
        assertEquals("005", (observer.values[1] as Resource.Error).code)

        // Validamos el mensaje de error
        assertEquals("Algún error", (observer.values[1] as Resource.Error).msg)
    }

    @Test
    fun `When onSearchQueryChanged is called, it should update searchQuery flow`() = runTest {
        val query = "NASA"

        // Simulamos una respuesta exitosa del caso de uso
        coEvery { articleUseCase.getArticles(any()) } returns Resource.Success(fakeArticleList)

        // Ejecutamos la función a probar
        viewModel.onSearchQueryChanged(query = query)

        // Verificamos que el query sea el mismo
        assertEquals(query, viewModel.onGetSearchQueryChanged())
    }

    @Test
    fun `When fetchArticles is called without reload or loadMore and has data, it should do nothing`() = runTest {
        // Simulamos una respuesta exitosa del caso de uso
        coEvery { articleUseCase.getArticles(any()) } returns Resource.Success(fakeArticleList)

        // Ejecutamos la función sin reload ni loadMore
        viewModel.fetchArticles(reload = false, loadMore = false)

        // Verificamos que no se volvió a llamar al caso de uso
        coVerify(exactly = 0) { articleUseCase.getArticles(any()) }
    }

}
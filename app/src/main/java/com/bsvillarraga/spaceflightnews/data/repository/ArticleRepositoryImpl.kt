package com.bsvillarraga.spaceflightnews.data.repository

import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.data.model.ArticleDto
import com.bsvillarraga.spaceflightnews.data.model.PaginationDto
import com.bsvillarraga.spaceflightnews.data.remote.ApiErrorResponse
import com.bsvillarraga.spaceflightnews.data.remote.ApiHelper
import com.bsvillarraga.spaceflightnews.data.remote.ApiResponse
import com.bsvillarraga.spaceflightnews.data.remote.ApiSuccessResponse
import com.bsvillarraga.spaceflightnews.data.remote.articles.ArticlesApiClient
import com.bsvillarraga.spaceflightnews.data.utils.PaginationManager
import com.bsvillarraga.spaceflightnews.domain.model.Article
import com.bsvillarraga.spaceflightnews.domain.model.ArticleDetail
import com.bsvillarraga.spaceflightnews.domain.repository.ArticleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementación del repositorio para manejar la obtención de artículos desde una API remota.
 *
 * Esta clase se encarga de realizar las llamadas a la API mediante "ArticlesApiClient",
 * gestionar la paginación con "PaginationManager" y manejar respuestas de la API de forma segura
 * utilizando "ApiHelper".
 *
 * @Importante: los parametros fueron inyectados desde el módulo DI (NetworkModule).
 */

class ArticleRepositoryImpl @Inject constructor(
    private val api: ArticlesApiClient,
    private val apiHelper: ApiHelper,
    private val paginationManager: PaginationManager
) : ArticleRepository {

    /**
     * Obtiene la lista de artículos desde la API.
     * @param query Consulta opcional para filtrar artículos.
     */
    override suspend fun getArticles(
        query: String?
    ): Resource<List<Article>> {
        return withContext(Dispatchers.IO) {
            val response: ApiResponse<PaginationDto> =
                apiHelper.safeApiCall {
                    api.getArticles(query, paginationManager.getCurrentOffset())
                }

            when (response) {
                is ApiSuccessResponse -> {
                    val pagination = response.body
                    paginationManager.updatePagination(pagination)
                    Resource.Success(pagination.articles.map { it.toArticleDomain() })
                }

                is ApiErrorResponse -> Resource.Error(response.code, response.msg, response.error)
            }
        }
    }

    /**
     * Obtiene un artículo específico por su ID.
     * @param articleId ID del artículo a obtener.
     */
    override suspend fun getArticleById(articleId: Long): Resource<ArticleDetail> {
        return withContext(Dispatchers.IO) {
            val response: ApiResponse<ArticleDto> =
                apiHelper.safeApiCall { api.getArticleById(articleId) }

            when (response) {
                is ApiSuccessResponse -> {
                    val article = response.body
                    Resource.Success(article.toArticleDetailDomain())
                }

                is ApiErrorResponse -> Resource.Error(response.code, response.msg, response.error)
            }
        }
    }
}
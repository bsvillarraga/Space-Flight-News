package com.bsvillarraga.spaceflightnews.data.remote.articles

import com.bsvillarraga.spaceflightnews.data.model.ArticleDto
import com.bsvillarraga.spaceflightnews.data.model.PaginationDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz de cliente API para obtener artículos desde el servicio web.
 */
interface ArticlesApiClient {
    /**
     * Obtiene una lista de articulos, ya sea paginasdos o filtrados
     *
     * @param query Consulta opcional para filtrar articulos.
     * @param offset Desplazamiento opcional para paginar los resultados.
     * */
    @GET("v4/articles")
    suspend fun getArticles(
        @Query("search") query: String? = null,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = 10,
        @Query("ordering") sort: Array<String>? = arrayOf("-published_at")
    ): Response<PaginationDto>

    /**
     * Obtiene un articulo por su ID
     *
     * @param articleId ID del articulo a obtener
     * */
    @GET("v4/articles/{id}")
    suspend fun getArticleById(@Path("id") articleId: Long): Response<ArticleDto>
}
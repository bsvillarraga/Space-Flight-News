package com.bsvillarraga.spaceflightnews.data.remote.articles

import com.bsvillarraga.spaceflightnews.data.model.ArticleDto
import com.bsvillarraga.spaceflightnews.data.model.PaginationDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticlesApiClient {
    @GET("v4/articles")
    suspend fun getArticles(
        @Query("search") query: String? = null,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = 10,
        @Query("ordering") sort: Array<String>? = arrayOf("-published_at")
    ): Response<PaginationDto>

    @GET("v4/articles/{id}")
    suspend fun getArticleById(@Path("id") articleId: Long): Response<ArticleDto>
}
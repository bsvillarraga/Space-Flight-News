package com.bsvillarraga.spaceflightnews.domain.repository

import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.model.Article
import com.bsvillarraga.spaceflightnews.domain.model.ArticleDetail

/**
 * Repositorio de artículos que define las operaciones de acceso a datos.
 */

interface ArticleRepository {
    suspend fun getArticles(
        query: String? = null
    ): Resource<List<Article>>

    suspend fun getArticleById(articleId: Long): Resource<ArticleDetail>
}
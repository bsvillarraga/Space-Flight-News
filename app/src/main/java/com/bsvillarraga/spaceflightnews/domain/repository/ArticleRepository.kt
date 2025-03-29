package com.bsvillarraga.spaceflightnews.domain.repository

import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.model.Article
import com.bsvillarraga.spaceflightnews.domain.model.ArticleDetail

interface ArticleRepository {
    suspend fun getArticles(
        query: String? = null
    ): Resource<List<Article>>

    suspend fun getArticleById(articleId: Long): Resource<ArticleDetail>
}
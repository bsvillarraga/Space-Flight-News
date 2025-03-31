package com.bsvillarraga.spaceflightnews.domain.usecase

import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.model.Article
import com.bsvillarraga.spaceflightnews.domain.repository.ArticleRepository
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    suspend fun getArticles(query: String? = null): Resource<List<Article>> {
        return repository.getArticles(query)
    }
}

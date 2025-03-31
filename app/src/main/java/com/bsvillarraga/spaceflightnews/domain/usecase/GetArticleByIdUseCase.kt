package com.bsvillarraga.spaceflightnews.domain.usecase

import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.model.ArticleDetail
import com.bsvillarraga.spaceflightnews.domain.repository.ArticleRepository
import javax.inject.Inject

class GetArticleByIdUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    suspend fun getArticleById(articleId: Long): Resource<ArticleDetail> {
        return repository.getArticleById(articleId)
    }
}
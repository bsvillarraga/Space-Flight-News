package com.bsvillarraga.spaceflightnews.domain.usecase

import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.model.ArticleDetail
import com.bsvillarraga.spaceflightnews.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetArticleByIdUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    fun getArticleById(articleId: Long): Flow<Resource<ArticleDetail>> {
        return flow {
            emit(Resource.Loading())
            emit(repository.getArticleById(articleId))
        }
    }
}
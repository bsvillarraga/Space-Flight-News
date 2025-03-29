package com.bsvillarraga.spaceflightnews.domain.usecase

import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.model.Article
import com.bsvillarraga.spaceflightnews.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    fun getArticles(query: String? = null, loadMore: Boolean): Flow<Resource<List<Article>>> {
        return flow {
            if (!loadMore) {
                emit(Resource.Loading())
            }

            emit(repository.getArticles(query))
        }
    }
}

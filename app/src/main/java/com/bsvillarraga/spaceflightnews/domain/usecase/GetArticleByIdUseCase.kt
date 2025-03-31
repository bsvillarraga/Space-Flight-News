package com.bsvillarraga.spaceflightnews.domain.usecase

import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.model.ArticleDetail
import com.bsvillarraga.spaceflightnews.domain.repository.ArticleRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener los detalles de un artículo por su ID.
 *
 * Este caso de uso actúa como un intermediario entre la capa de dominio y el repositorio,
 * permitiendo recuperar la información de un artículo específico.
 */

class GetArticleByIdUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    suspend fun getArticleById(articleId: Long): Resource<ArticleDetail> {
        return repository.getArticleById(articleId)
    }
}
package com.bsvillarraga.spaceflightnews.presentation.ui.articles.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.model.ArticleDetail
import com.bsvillarraga.spaceflightnews.domain.usecase.GetArticleByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailsViewModel @Inject constructor(
    private val articleById: GetArticleByIdUseCase
) : ViewModel() {
    private var hasLoaded = false

    private val _article = MutableLiveData<Resource<ArticleDetail>>()
    val article: LiveData<Resource<ArticleDetail>> = _article

    /**
     * Obtiene el artículo seleccionado.
     * @param id busca el artículo por id.
     * @param reload Indica si se deben recargar los datos. Usado en caso de error
     */
    fun fetchArticleById(id: Long, reload: Boolean = false) {
        if (hasLoaded && !reload) return
        _article.value = Resource.Loading()

        viewModelScope.launch {
            _article.value = articleById.getArticleById(id)
            hasLoaded = true
        }
    }
}
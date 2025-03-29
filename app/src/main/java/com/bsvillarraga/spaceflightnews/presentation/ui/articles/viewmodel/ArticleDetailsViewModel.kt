package com.bsvillarraga.spaceflightnews.presentation.ui.articles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.model.ArticleDetail
import com.bsvillarraga.spaceflightnews.domain.usecase.GetArticleByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailsViewModel @Inject constructor(
    private val articleById: GetArticleByIdUseCase
) : ViewModel() {
    private var hasLoaded = false

    private val _article = MutableStateFlow<Resource<ArticleDetail>>(Resource.Loading())
    val article: StateFlow<Resource<ArticleDetail>> = _article

    fun fetchArticleById(id: Long) {
        if (hasLoaded) return

        viewModelScope.launch {
            articleById.getArticleById(id).collect { result ->
                _article.value = result
                hasLoaded = true
            }
        }
    }
}
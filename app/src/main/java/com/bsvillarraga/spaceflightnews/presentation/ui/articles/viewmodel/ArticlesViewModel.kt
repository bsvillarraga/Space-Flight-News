package com.bsvillarraga.spaceflightnews.presentation.ui.articles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.domain.model.Article
import com.bsvillarraga.spaceflightnews.domain.usecase.GetArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val articleUseCase: GetArticlesUseCase
) : ViewModel() {

    private val _articles = MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val articles: StateFlow<Resource<List<Article>>> = _articles

    private val _searchQuery = MutableStateFlow("")
    private val _currentList = mutableListOf<Article>()

    init {
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    _currentList.clear()
                    fetchArticles(query.ifEmpty { null }, reload = true)
                }
        }
    }

    fun fetchArticles(query: String? = null, reload: Boolean = false, loadMore: Boolean = false) {
        if (!reload && !loadMore && _currentList.isNotEmpty()) return

        viewModelScope.launch {
            articleUseCase.getArticles(query ?: _searchQuery.value, loadMore).collect { result ->
                handleResult(result, loadMore)
            }
        }
    }

    private fun handleResult(result: Resource<List<Article>>, loadMore: Boolean) {
        when (result) {
            is Resource.Success -> {
                if (!loadMore) _currentList.clear()
                _currentList.addAll(result.data ?: listOf())
                _articles.value = Resource.Success(_currentList.toList())
            }

            is Resource.Error -> _articles.value = result
            is Resource.Loading -> _articles.value = Resource.Loading()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}

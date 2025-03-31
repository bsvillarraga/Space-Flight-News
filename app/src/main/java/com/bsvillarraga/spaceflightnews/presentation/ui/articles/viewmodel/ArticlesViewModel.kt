package com.bsvillarraga.spaceflightnews.presentation.ui.articles.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _articles = MutableLiveData<Resource<List<Article>>>(Resource.Loading())
    val articles: LiveData<Resource<List<Article>>> = _articles

    private val _searchQuery = MutableStateFlow("")
    private val _currentList = mutableListOf<Article>()

    init {
        observeSearchQuery()
    }

    /**
     * Observa los cambios en la consulta de búsqueda y actualiza la lista de artículos en consecuencia.
     * Se usa "debounce" para evitar llamadas excesivas y "distinctUntilChanged" para evitar duplicados.
     */
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery.debounce(300).distinctUntilChanged().collectLatest { query ->
                _currentList.clear()
                fetchArticles(query.ifEmpty { null }, reload = true)
            }
        }
    }

    /**
     * Obtiene la lista de artículos en función de la consulta de búsqueda.
     * @param query La consulta de búsqueda (opcional).
     * @param reload Indica si se deben recargar los datos.
     * @param loadMore Indica si se deben cargar más artículos en la lista actual.
     */
    fun fetchArticles(query: String? = null, reload: Boolean = false, loadMore: Boolean = false) {
        if (!reload && !loadMore && _currentList.isNotEmpty()) return

        if (reload) {
            _articles.value = Resource.Loading()
        }

        viewModelScope.launch {
            handleResult(articleUseCase.getArticles(query ?: onGetSearchQueryChanged()), loadMore)
        }
    }

    /**
     * Maneja el resultado de la consulta de artículos y actualiza el estado de la UI.
     * @param result El resultado de la consulta.
     * @param loadMore Indica si se deben agregar más artículos a la lista actual.
     */
    private fun handleResult(result: Resource<List<Article>>, loadMore: Boolean) {
        when (result) {
            is Resource.Success -> {
                if (!loadMore) {
                    _currentList.clear()
                }

                _currentList.addAll(result.data ?: listOf())
                _articles.value = Resource.Success(_currentList.toList())
            }

            is Resource.Error -> _articles.value = result
            is Resource.Loading -> _articles.value = Resource.Loading()
        }
    }

    /**
     * Actualiza la consulta de búsqueda.
     * @param query Nueva consulta de búsqueda ingresada por el usuario.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onGetSearchQueryChanged(): String {
        return _searchQuery.value
    }
}

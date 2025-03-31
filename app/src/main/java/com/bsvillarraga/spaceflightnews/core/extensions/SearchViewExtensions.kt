package com.bsvillarraga.spaceflightnews.core.extensions

import androidx.appcompat.widget.SearchView

/**
 * Función de extensión para configurar un OnQueryTextListener en un SearchView.
 * */
fun SearchView.queryTextListener(onSubmit: (String) -> Unit, onTextChanged: (String?) -> Unit) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let {
                onSubmit.invoke(query)
            }
            return true
        }

        override fun onQueryTextChange(query: String?): Boolean {
            if (query.isNullOrEmpty()) {
                onTextChanged.invoke(query)
            }
            return true
        }
    })
}
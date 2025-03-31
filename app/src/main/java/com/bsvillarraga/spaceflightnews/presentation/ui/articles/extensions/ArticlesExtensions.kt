package com.bsvillarraga.spaceflightnews.presentation.ui.articles.extensions

import androidx.core.view.isVisible
import com.bsvillarraga.spaceflightnews.databinding.FragmentArticleDetailsBinding
import com.bsvillarraga.spaceflightnews.databinding.FragmentArticlesBinding

fun FragmentArticlesBinding.showState(
    showLoading: Boolean = false,
    showError: Boolean = false,
    showNoInfo: Boolean = false
) {
    contentInformation.root.isVisible = showError || showNoInfo
    contentLoading.root.isVisible = showLoading
    rcvArticles.isVisible = !(showLoading || showError || showNoInfo)

    contentInformation.apply {
        contentError.root.isVisible = showError
        contentWithoutInformation.root.isVisible = showNoInfo
    }
}

fun FragmentArticleDetailsBinding.showState(
    showLoading: Boolean = false,
    showError: Boolean = false,
) {
    motionLayout.isVisible = !(showLoading || showError)
    contentLoading.root.isVisible = showLoading
    contentInformation.root.isVisible = showError

    contentInformation.apply {
        contentError.root.isVisible = showError
        contentWithoutInformation.root.isVisible = false
    }
}
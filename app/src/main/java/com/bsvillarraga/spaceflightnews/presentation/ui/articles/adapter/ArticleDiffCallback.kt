package com.bsvillarraga.spaceflightnews.presentation.ui.articles.adapter

import androidx.recyclerview.widget.DiffUtil
import com.bsvillarraga.spaceflightnews.domain.model.Article

class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }

}
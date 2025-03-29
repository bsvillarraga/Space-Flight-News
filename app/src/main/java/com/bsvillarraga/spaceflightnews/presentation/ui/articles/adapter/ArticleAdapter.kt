package com.bsvillarraga.spaceflightnews.presentation.ui.articles.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bsvillarraga.spaceflightnews.databinding.ItemArticleBinding
import com.bsvillarraga.spaceflightnews.databinding.ItemLoadingBinding
import com.bsvillarraga.spaceflightnews.domain.model.Article

class ArticleAdapter(
    private val onItemClicked: (Article) -> Unit
) : ListAdapter<Article, RecyclerView.ViewHolder>(ArticleDiffCallback()) {
    private val viewTypeItem = 0
    private val viewTypeLoading = 1
    private var isLoading = false

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) viewTypeItem else viewTypeLoading
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (isLoading) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewTypeItem) {
            ArticleViewHolder(
                ItemArticleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            val binding =
                ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            LoadingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArticleViewHolder) {
            val article = getItem(position)
            holder.bind(article)

            holder.itemView.setOnClickListener {
                onItemClicked(article)
            }
        }
    }

    fun setLoading(isLoading: Boolean) {
        if (this.isLoading == isLoading) return
        this.isLoading = isLoading

        if (isLoading) {
            notifyItemInserted(super.getItemCount())
        } else {
            notifyItemRemoved(super.getItemCount())
        }
    }
}
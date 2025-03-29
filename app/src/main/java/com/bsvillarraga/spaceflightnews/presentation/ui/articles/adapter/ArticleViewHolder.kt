package com.bsvillarraga.spaceflightnews.presentation.ui.articles.adapter

import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bsvillarraga.spaceflightnews.R
import com.bsvillarraga.spaceflightnews.core.extensions.toFormattedDate
import com.bsvillarraga.spaceflightnews.databinding.ItemArticleBinding
import com.bsvillarraga.spaceflightnews.domain.model.Article
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class ArticleViewHolder(private val binding: ItemArticleBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(article: Article) {
        binding.apply {
            progressBar.visibility = View.VISIBLE

            Glide.with(root.context)
                .load(article.imageUrl)
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        imgArticle.visibility = View.VISIBLE
                        return false
                    }
                })
                .into(imgArticle)

            tvDate.text =
                root.context.getString(R.string.published_at, article.publishedAt.toFormattedDate())
            titleText.text = article.title
        }
    }
}
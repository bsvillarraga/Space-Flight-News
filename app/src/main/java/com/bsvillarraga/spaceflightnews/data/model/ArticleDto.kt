package com.bsvillarraga.spaceflightnews.data.model

import com.bsvillarraga.spaceflightnews.domain.model.Article
import com.bsvillarraga.spaceflightnews.domain.model.ArticleDetail
import com.google.gson.annotations.SerializedName

data class ArticleDto(
    val id: Long,
    val title: String,
    val authors: List<AuthorDto>,
    val url: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("news_site")
    val newsSite: String,
    val summary: String,
    @SerializedName("published_at")
    val publishedAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val featured: Boolean,
    val launches: List<LaunchDto>,
    val events: List<EventDto>,
) {
    fun toArticleDomain(): Article = Article(
        id = id,
        title = title,
        imageUrl = imageUrl,
        newsSite = newsSite,
        publishedAt = publishedAt
    )

    fun toArticleDetailDomain(): ArticleDetail = ArticleDetail(
        id = id,
        title = title,
        authors = authors.map { it.toDomain() },
        url = url,
        newsSite = newsSite,
        imageUrl = imageUrl,
        summary = summary,
        publishedAt = publishedAt,
        updatedAt = updatedAt
    )
}
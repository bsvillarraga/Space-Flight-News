package com.bsvillarraga.spaceflightnews.domain.model

data class ArticleDetail(
    val id: Long,
    val title: String,
    val authors: List<Author>,
    val url: String,
    val newsSite: String,
    val imageUrl: String,
    val summary: String,
    val publishedAt: String,
    val updatedAt: String
)

package com.bsvillarraga.spaceflightnews.domain.model

data class Article(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val newsSite: String,
    val publishedAt: String
)

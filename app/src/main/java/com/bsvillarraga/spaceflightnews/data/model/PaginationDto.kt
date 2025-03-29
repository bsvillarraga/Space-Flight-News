package com.bsvillarraga.spaceflightnews.data.model

import com.google.gson.annotations.SerializedName

data class PaginationDto(
    val count: Long,
    val next: String? = null,
    val previous: String,
    @SerializedName("results")
    val articles: List<ArticleDto>
)

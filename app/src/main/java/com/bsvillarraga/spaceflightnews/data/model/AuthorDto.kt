package com.bsvillarraga.spaceflightnews.data.model

import com.bsvillarraga.spaceflightnews.domain.model.Author

data class AuthorDto(
    val name: String,
    val socials: SocialDto?,
) {
    fun toDomain(): Author = Author(
        name = name
    )
}
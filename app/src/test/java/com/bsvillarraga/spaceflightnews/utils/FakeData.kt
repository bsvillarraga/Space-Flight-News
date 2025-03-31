package com.bsvillarraga.spaceflightnews.utils

import com.bsvillarraga.spaceflightnews.data.model.ArticleDto
import com.bsvillarraga.spaceflightnews.data.model.EventDto
import com.bsvillarraga.spaceflightnews.data.model.LaunchDto
import com.bsvillarraga.spaceflightnews.data.model.PaginationDto

object FakeData {
    val fakePaginationWithLaunchesAndEvents: PaginationDto = PaginationDto(
        count = 1,
        next = "https://www.test1.com",
        previous = "https://www.test1.com",
        articles = listOf(
            ArticleDto(
                id = 1,
                title = "Test 1",
                authors = listOf(),
                url = "https://www.test1.com",
                imageUrl = "https://www.test1.com/image.jpg",
                newsSite = "Test 1",
                summary = "Test description 1",
                publishedAt = "2023-01-01T00:00:00Z",
                updatedAt = "2023-01-01T00:00:00Z",
                featured = false,
                launches = listOf(
                    LaunchDto(
                        launchId = "1",
                        provider = "Test 1"
                    )
                ),
                events = listOf(
                    EventDto(
                        eventId = "1",
                        provider = "Test 1"
                    )
                )
            )
        )
    )

    val fakeArticleList = fakePaginationWithLaunchesAndEvents.articles.map { it.toArticleDomain() }

    val fakePaginationWithoutArticles: PaginationDto = PaginationDto(
        count = 0,
        next = null,
        previous = "https://www.test1.com",
        articles = listOf()
    )

    val fakeArticle: ArticleDto = ArticleDto(
        id = 1,
        title = "Test 1",
        authors = listOf(),
        url = "https://www.test1.com",
        imageUrl = "https://www.test1.com/image.jpg",
        newsSite = "Test 1",
        summary = "Test description 1",
        publishedAt = "2023-01-01T00:00:00Z",
        updatedAt = "2023-01-01T00:00:00Z",
        featured = false,
        launches = listOf(
            LaunchDto(
                launchId = "1",
                provider = "Test 1"
            )
        ),
        events = listOf(
            EventDto(
                eventId = "1",
                provider = "Test 1"
            )
        )
    )

    val fakeArticleDetail = fakeArticle.toArticleDetailDomain()

}
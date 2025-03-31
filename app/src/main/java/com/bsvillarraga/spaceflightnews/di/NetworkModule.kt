package com.bsvillarraga.spaceflightnews.di

import android.content.Context
import com.bsvillarraga.spaceflightnews.core.network.NetworkHelper
import com.bsvillarraga.spaceflightnews.data.local.dao.PaginationDao
import com.bsvillarraga.spaceflightnews.data.remote.ApiHelper
import com.bsvillarraga.spaceflightnews.data.remote.articles.ArticlesApiClient
import com.bsvillarraga.spaceflightnews.data.repository.ArticleRepositoryImpl
import com.bsvillarraga.spaceflightnews.data.utils.PaginationManager
import com.bsvillarraga.spaceflightnews.domain.repository.ArticleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * En este archivo se la inyección de dependencias de la librería Retrofit,
 * configuración de la url base, cliente OkHttpClient personalizado,
 * y provider de los servicios de la API.
 * */

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val originalUrl = original.url()

                val newUrl = originalUrl.newBuilder()
                    .addQueryParameter("format", "json")
                    .build()

                val newRequest = original.newBuilder()
                    .url(newUrl)
                    .build()

                chain.proceed(newRequest)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.spaceflightnewsapi.net/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provider de los servicios de la API.
     * */

    @Provides
    @Singleton
    fun provideNetworkHelper(@ApplicationContext context: Context): NetworkHelper {
        return NetworkHelper(context)
    }

    @Provides
    @Singleton
    fun provideApiHelper(networkHelper: NetworkHelper): ApiHelper {
        return ApiHelper(networkHelper)
    }

    @Provides
    @Singleton
    fun providePaginationManager(paginationDao: PaginationDao): PaginationManager {
        return PaginationManager(paginationDao)
    }

    @Singleton
    @Provides
    fun provideArticlesApiClient(retrofit: Retrofit): ArticlesApiClient =
        retrofit.create(ArticlesApiClient::class.java)

    @Provides
    @Singleton
    fun provideArticleRepository(
        api: ArticlesApiClient,
        apiHelper: ApiHelper,
        paginationManager: PaginationManager
    ): ArticleRepository =
        ArticleRepositoryImpl(api, apiHelper, paginationManager)
}
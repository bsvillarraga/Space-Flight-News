package com.bsvillarraga.spaceflightnews.di

import android.content.Context
import androidx.room.Room
import com.bsvillarraga.spaceflightnews.data.local.SpaceFlightNewsDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * En este archivo se realiza la inyección de dependencias de la base de datos
 * y acceso a los DAOs
 * */

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    private const val SPACE_FLIGHT_NEW = "space_flight_news_database"

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, SpaceFlightNewsDb::class.java, SPACE_FLIGHT_NEW).build()

    /**
     * Provider de los servicios de la API.
     * */

    @Singleton
    @Provides
    fun providePaginationDao(db: SpaceFlightNewsDb) = db.paginationDao()
}
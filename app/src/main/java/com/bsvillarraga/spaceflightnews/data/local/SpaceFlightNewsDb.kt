package com.bsvillarraga.spaceflightnews.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bsvillarraga.spaceflightnews.data.local.dao.PaginationDao
import com.bsvillarraga.spaceflightnews.data.local.entities.PaginationEntity

@Database(entities = [PaginationEntity::class], version = 1)

abstract class SpaceFlightNewsDb: RoomDatabase() {
    abstract fun paginationDao(): PaginationDao
}
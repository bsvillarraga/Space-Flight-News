package com.bsvillarraga.spaceflightnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bsvillarraga.spaceflightnews.data.local.entities.PaginationEntity

@Dao
interface PaginationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPagination(pagination: PaginationEntity)

    @Query("DELETE FROM pagination")
    suspend fun deletePagination()

    @Query("SELECT * FROM pagination")
    suspend fun getPagination(): PaginationEntity?
}
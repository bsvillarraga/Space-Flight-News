package com.bsvillarraga.spaceflightnews.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pagination")
data class PaginationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val count: Long = 0,
    val offset: Int? = null
)

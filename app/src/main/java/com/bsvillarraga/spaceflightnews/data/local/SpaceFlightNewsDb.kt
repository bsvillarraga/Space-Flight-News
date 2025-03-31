package com.bsvillarraga.spaceflightnews.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bsvillarraga.spaceflightnews.data.local.dao.PaginationDao
import com.bsvillarraga.spaceflightnews.data.local.entities.PaginationEntity


/**
 * Configuración de la base de datos de la aplicación.
 *
 * Este archivo define las entidades, la versión de la base de datos y
 * prepara los DAOs para cada entidad. Además, configura su inyección de
 * dependencias en el módulo DI (RoomModule).
 */
@Database(entities = [PaginationEntity::class], version = 1)

abstract class SpaceFlightNewsDb: RoomDatabase() {
    abstract fun paginationDao(): PaginationDao
}
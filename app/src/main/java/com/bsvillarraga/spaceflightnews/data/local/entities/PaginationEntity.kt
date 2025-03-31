package com.bsvillarraga.spaceflightnews.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa la información de paginación en la base de datos local.
 *
 * Utilizada para almacenar el estado de la paginación, permitiendo recuperar la última
 * posición consultada en las peticiones paginadas.
 *
 * @property id Identificador único de la entidad (se genera automáticamente).
 * @property count Cantidad total de elementos disponibles en la paginación.
 * @property offset Índice del siguiente conjunto de datos a solicitar (puede ser "null").
 */

@Entity(tableName = "pagination")
data class PaginationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val count: Long = 0,
    val offset: Int? = null
)

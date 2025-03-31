package com.bsvillarraga.spaceflightnews.data.utils

import android.net.Uri
import com.bsvillarraga.spaceflightnews.data.local.dao.PaginationDao
import com.bsvillarraga.spaceflightnews.data.local.entities.PaginationEntity
import com.bsvillarraga.spaceflightnews.data.model.PaginationDto
import javax.inject.Inject

/**
 * Maneja la actualización de la paginación de la base de datos local
 * y proporciona la siguiente paginación.
 * */
class PaginationManager @Inject constructor(private val paginationDao: PaginationDao) {
    suspend fun getCurrentOffset(): Int? {
        return paginationDao.getPagination()?.offset
    }

    suspend fun updatePagination(paginationDto: PaginationDto) {
        val offset = extractOffset(paginationDto.next)

        paginationDao.deletePagination()

        if (paginationDto.articles.isNotEmpty()) {
            paginationDao.insertPagination(
                PaginationEntity(
                    count = paginationDto.count,
                    offset = offset
                )
            )
        }
    }

    private fun extractOffset(url: String?): Int? {
        return url?.let { Uri.parse(it).getQueryParameter("offset")?.toIntOrNull() }
    }
}
package com.tigerspike.bitbucketcodemetrics.model

import java.lang.AssertionError

data class Paginated<T>(
    val pagelen: Int,
    val size: Int,
    val page: Int,
    val next: String?,
    val values: List<T>
) {
    fun hasNextPage() = pagelen * page < size || !next.isNullOrEmpty()
    fun hasPreviousPage() = page > 1

    lateinit var pageLoader: suspend (Int) -> Paginated<T>
    suspend fun loadNextPage() = if (hasNextPage()) pageLoader(page + 1) else null
    suspend fun loadPreviousPage() = if (hasPreviousPage()) pageLoader(page - 1) else null

    suspend fun loadAll(): List<T> {
        if (page != 1) return values

        val allValues = mutableListOf<T>()
        var nextPage: Paginated<T>? = this
        do {
            nextPage?.let { allValues.addAll(it.values) }
            nextPage = nextPage?.loadNextPage()
        } while (nextPage != null)
        return allValues
    }
}


package com.tigerspike.bitbucketcodemetrics.model

data class Paginated<T>(
    val pagelen: Int,
    val size: Int,
    val page: Int,
    val values: List<T>
) {
    fun hasNextPage() = pagelen * page < size
    fun hasPreviousPage() = page > 1

    lateinit var pageLoader: suspend (Int) -> Paginated<T>
    suspend fun loadNextPage() = if (hasNextPage()) pageLoader(page + 1) else null
    suspend fun loadPreviousPage() = if (hasPreviousPage()) pageLoader(page - 1) else null
}


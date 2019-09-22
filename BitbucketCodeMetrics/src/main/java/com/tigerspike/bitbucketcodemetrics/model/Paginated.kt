package com.tigerspike.bitbucketcodemetrics.model

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

data class Paginated<T>(
    val pagelen: Int,
    val size: Int,
    val page: String,
    val next: String?,
    val values: List<T>
) {
    fun hasNextPage() = !next.isNullOrEmpty()

    lateinit var pageLoader: suspend (String) -> Paginated<T>
    suspend fun loadNextPage() = extractNextPage()?.let { pageLoader(it) }

    private fun extractNextPage(): String? {
        if (next == null) return null
        return next.toHttpUrlOrNull()?.queryParameter("page")
    }

    suspend fun loadAll(limit: Int? = null): List<T> {
        var breakWhenZero = limit
        val allValues = mutableListOf<T>()
        var nextPage: Paginated<T>? = this
        do {
            nextPage?.let { allValues.addAll(it.values) }

            breakWhenZero = breakWhenZero?.let {
                if (it <= 1) {
                    return allValues
                }
                return@let it - 1
            }

            nextPage = nextPage?.loadNextPage()
        } while (nextPage != null)
        return allValues
    }
}


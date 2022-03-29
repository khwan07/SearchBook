package com.test.searchbook.presentation.search

import android.util.Log
import com.test.searchbook.data.api.model.Book
import com.test.searchbook.data.api.model.SearchResult
import java.util.concurrent.atomic.AtomicInteger

class PagingController {
    companion object {
        const val MAX_LOAD_COUNT = 3
    }

    private val loadProgress = AtomicInteger(0)
    private val queries = mutableListOf<QueryInfo>()
    private var exclusiveQuery: QueryInfo? = null

    var key: String = ""
    val page: Int
        get() = queries.sumOf { it.page }
    val total: Int
        get() = queries.sumOf { it.total }
    val count: Int
        get() = queries.sumOf { it.count }

    fun nextPage(query: String): Int {
        return queries.find { it.query == query && !it.isLastPage() }?.nextQueryPage() ?: 0
    }

    fun isLastPage(): Boolean {
        return queries.all { it.isLastPage() }
    }

    fun isMaxLoading(): Boolean {
        return loadProgress.get() == MAX_LOAD_COUNT
    }

    fun isLoading(): Boolean {
        return loadProgress.get() > 0
    }

    fun validQuery(): String? {
        return queries.find { !it.isLastPage() }?.query
    }

    fun hasNextQuery(): Boolean {
        return queries.any { !it.isLastPage() }
    }

    fun incrementLoading() {
        loadProgress.incrementAndGet()
    }

    fun decrementLoading() {
        loadProgress.decrementAndGet()
    }

    fun setQuery(query: String) {
        val orIndex = query.indexOf("|").takeIf { it != -1 } ?: Int.MAX_VALUE
        val exclusiveIndex = query.indexOf("-").takeIf { it != -1 } ?: Int.MAX_VALUE

        when {
            orIndex < exclusiveIndex -> {
                query.substring(0, orIndex).also {
                    queries.add(QueryInfo(it))
                }
                if (orIndex + 1 < query.lastIndex) {
                    query.substring(orIndex + 1, query.length).also {
                        queries.add(QueryInfo(it))
                    }
                }
            }
            exclusiveIndex < orIndex -> {
                query.substring(0, exclusiveIndex).also {
                    queries.add(QueryInfo(it))
                }
                if (exclusiveIndex + 1 < query.lastIndex) {
                    query.substring(exclusiveIndex + 1, query.length).also {
                        exclusiveQuery = QueryInfo(it)
                    }
                }
            }
            else -> {
                queries.add(QueryInfo(query))
            }
        }
        Log.d("PageInfo", "setQuery:$queries, exclusive:$exclusiveQuery")
    }

    fun setResult(result: SearchResult, query: String) {
        val index = queries.indexOfFirst { it.query == query && !it.isLastPage() }
        if (index != -1) {
            queries[index].set(result)
        }
    }

    fun filterExclusive(list: List<Book>): List<Book> {
        return if (exclusiveQuery != null) {
            list.filterNot { it.title.contains(exclusiveQuery!!.query, ignoreCase = true) }
        } else {
            list
        }
    }
}


class QueryInfo(var query: String) {
    var page: Int = 0
    var total: Int = -1
    var count: Int = 0
    private var queryPage: Int = 0

    fun set(result: SearchResult) {
        page = kotlin.runCatching {
            result.page.toInt()
        }.getOrDefault(0)
        total = kotlin.runCatching {
            result.total.toInt()
        }.getOrDefault(0)
        count += result.books.size
        Log.d("QueryInfo", "set page:$page, total:$total, count:$count")
    }

    fun isLastPage(): Boolean {
        return total != -1 && count >= total
    }

    fun nextQueryPage(): Int {
        return ++queryPage
    }
}
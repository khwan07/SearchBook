package com.test.searchbook.data.api.model

data class SearchResult(
    val total: String,
    val page: String,
    val books: List<Book>
) {
    companion object {
        val EMPTY = SearchResult("", "", emptyList())
    }
}
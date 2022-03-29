package com.test.searchbook.presentation.search

import com.test.searchbook.data.api.model.Book

sealed class ViewItem(val id: Long, val viewType: ViewType) {
    class BookItem(id: Long, val data: Book) : ViewItem(id, ViewType.BOOK)

    class LoadingItem(id: Long) : ViewItem(id, ViewType.LOADING)
}

enum class ViewType {
    BOOK, LOADING
}

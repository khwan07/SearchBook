package com.test.searchbook.repository

import com.test.searchbook.data.api.BookApi
import com.test.searchbook.data.api.model.BookDetail
import com.test.searchbook.data.api.model.SearchResult
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor() {

    @Inject
    lateinit var bookApi: BookApi

    fun searchBook(query: String, page: String): Single<SearchResult> {
        return bookApi.search(query, page)
            .map {
                if (!it.isSuccessful) {
                    throw IllegalStateException("searchBook error:${it.code()}")
                }
                it.body() ?: throw IllegalArgumentException("searchBook body is null")
            }
    }

    fun bookDetail(isbn13: String): Single<BookDetail> {
        return bookApi.booksDetail(isbn13)
            .map {
                if (!it.isSuccessful) {
                    throw IllegalStateException("bookDetail error:${it.code()}")
                }
                it.body() ?: throw IllegalArgumentException("bookDetail body is null")
            }
    }
}
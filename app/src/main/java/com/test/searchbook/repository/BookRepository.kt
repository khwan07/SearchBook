package com.test.searchbook.repository

import android.util.Log
import com.test.searchbook.data.api.BookApi
import com.test.searchbook.data.api.model.SearchResult
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor() {

    companion object {
        private val TAG = BookRepository::class.java.simpleName
    }

    @Inject
    lateinit var bookApi: BookApi

    fun searchBook(query: String, page: String): Single<SearchResult> {
        return bookApi.search(query, page)
            .doOnSuccess {
                Log.d(TAG, "searchBook success:${it.body()}")
            }
            .map {
                it.body() ?: throw IllegalArgumentException("body is null")
            }
    }
}
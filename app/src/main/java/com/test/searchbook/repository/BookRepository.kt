package com.test.searchbook.repository

import android.util.Log
import com.test.searchbook.data.api.BookApi
import com.test.searchbook.data.api.model.BookDetail
import com.test.searchbook.data.api.model.SearchResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor() {

    @Inject
    lateinit var bookApi: BookApi

    private var searchCallQueue: LinkedBlockingQueue<Call<SearchResult>> = LinkedBlockingQueue()
    private var bookDetailCall: Call<BookDetail>? = null

    interface ApiCallback<T> {
        fun onSuccess(data: T)
        fun onFailure(t: Throwable)
    }

    fun searchBook(query: String, page: String, callback: ApiCallback<SearchResult>) {

        val call = bookApi.search(query, page)
        searchCallQueue.offer(call)
        call.enqueue(object : Callback<SearchResult> {
            override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                Log.d("BookRepository", "searchResponse ${response.body()?.page} thread:${Thread.currentThread().name}")
                searchCallQueue.poll()
                if (!response.isSuccessful) {
                    callback.onFailure(IllegalStateException("search error:${response.code()}"))
                    return
                }
                val data = response.body()
                if (data != null) {
                    callback.onSuccess(data)
                } else {
                    callback.onFailure(IllegalArgumentException("search body is null"))
                }
            }

            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                searchCallQueue.poll()
                callback.onFailure(t)
            }
        })
    }

    fun bookDetail(isbn13: String, callback: ApiCallback<BookDetail>) {
        bookDetailCall = bookApi.booksDetail(isbn13)
        bookDetailCall!!.enqueue(object : Callback<BookDetail> {
            override fun onResponse(call: Call<BookDetail>, response: Response<BookDetail>) {
                if (!response.isSuccessful) {
                    callback.onFailure(IllegalStateException("bookDetail error:${response.code()}"))
                    return
                }
                val data = response.body()
                if (data != null) {
                    callback.onSuccess(data)
                } else {
                    callback.onFailure(IllegalArgumentException("bookDetail body is null"))
                }
            }

            override fun onFailure(call: Call<BookDetail>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    fun cancelSearch() {
        while (searchCallQueue.isNotEmpty()) {
            searchCallQueue.poll()?.cancel()
        }
    }
}
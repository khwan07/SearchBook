package com.test.searchbook.data.api

import com.test.searchbook.data.api.model.BookDetail
import com.test.searchbook.data.api.model.SearchResult
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BookApi {
    @GET("books/{isbn13}")
    fun booksDetail(
        @Path("isbn13") isbn13: String
    ): Single<Response<BookDetail>>

    @GET("search/{query}/{page}")
    fun search(
        @Path("query") query: String,
        @Path("page") page: String
    ): Single<Response<SearchResult>>
}
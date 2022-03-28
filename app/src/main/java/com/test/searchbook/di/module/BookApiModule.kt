package com.test.searchbook.di.module

import com.test.searchbook.data.api.BookApi
import com.test.searchbook.network.ApiProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BookApiModule {
    @Provides
    @Singleton
    fun provideBookApi(apiProvider: ApiProvider): BookApi {
        return apiProvider.createApi(BookApi::class.java, ApiProvider.BOOK_API_HOST)
    }
}
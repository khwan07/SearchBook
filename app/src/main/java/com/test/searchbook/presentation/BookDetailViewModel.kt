package com.test.searchbook.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.test.searchbook.data.api.model.BookDetail
import com.test.searchbook.repository.BookRepository
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject

class BookDetailViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    @Inject
    lateinit var bookRepository: BookRepository

    fun getBookDetail(isbn13: String): Maybe<BookDetail> {
        return bookRepository.bookDetail(isbn13)
            .toMaybe()
            .onErrorResumeNext { Maybe.empty() }
    }

}
package com.test.searchbook.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.test.searchbook.data.api.model.BookDetail
import com.test.searchbook.repository.BookRepository
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject

class BookDetailViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    @Inject
    lateinit var bookRepository: BookRepository

    val error: MutableLiveData<Throwable> = MutableLiveData()

    fun getBookDetail(isbn13: String): Maybe<BookDetail> {
        return bookRepository.bookDetail(isbn13)
            .toMaybe()
            .doOnError { error.postValue(it) }
            .onErrorResumeNext { Maybe.empty() }
    }

}
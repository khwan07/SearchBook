package com.test.searchbook.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.test.searchbook.data.api.model.BookDetail
import com.test.searchbook.repository.BookRepository
import javax.inject.Inject

class BookDetailViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    @Inject
    lateinit var bookRepository: BookRepository

    val error: MutableLiveData<Throwable> = MutableLiveData()
    val bookDetail: MutableLiveData<BookDetail> = MutableLiveData()

    fun getBookDetail(isbn13: String) {
        bookRepository.bookDetail(isbn13, object : BookRepository.ApiCallback<BookDetail> {
            override fun onSuccess(data: BookDetail) {
                bookDetail.postValue(data)
            }

            override fun onFailure(t: Throwable) {
                error.postValue(t)
            }
        })
    }

}
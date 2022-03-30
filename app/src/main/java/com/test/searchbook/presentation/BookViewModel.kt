package com.test.searchbook.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.test.searchbook.presentation.search.PagingController
import com.test.searchbook.presentation.search.ViewItem
import com.test.searchbook.presentation.search.ViewType
import com.test.searchbook.repository.BookRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

class BookViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    companion object {
        private val TAG = BookViewModel::class.java.simpleName
    }

    @Inject
    lateinit var bookRepository: BookRepository

    private val searchDisposables = CompositeDisposable()
    private val loadingViewItem = ViewItem.LoadingItem(Long.MAX_VALUE)
    private var pagingController = PagingController()
    val bookList: MutableLiveData<List<ViewItem>> = MutableLiveData(listOf())
    val error: MutableLiveData<Throwable> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)


    override fun onCleared() {
        searchDisposables.clear()
    }

    fun searchNextPage(query: String) {
        if (pagingController.key != query) {
            pagingController = PagingController()
            pagingController.setQuery(query)
            searchDisposables.clear()
            bookList.postValue(emptyList())
            showLoading(true)
        } else if (pagingController.isLastPage()) {
            return
        }

        val validQuery = pagingController.validQuery() ?: return
        val page = pagingController.nextPage(validQuery)
        Log.d(TAG, "query page:$page, validQuery:$validQuery")

        pagingController.incrementLoading()
        bookRepository.searchBook(query, page.toString())
            .subscribeBy(
                onError = {
                    Log.e(TAG, "search error : ${it.message}")
                    it.printStackTrace()
                    cancelPendingPage()
                    error.postValue(it)
                    showLoading(false)
                },
                onSuccess = { result ->
                    val append = pagingController.key == query

                    pagingController.key = query
                    val loadProgress = pagingController.decrementLoading()
                    pagingController.setResult(result, validQuery)
                    Log.d(
                        TAG,
                        "total:${result.total}, page:${pagingController.page}, count:${pagingController.count}, size:${result.books.size}, loadProgress:${loadProgress}"
                    )

                    var list = if (append && bookList.value != null) {
                        val offset =
                            bookList.value!!.lastOrNull { it.viewType != ViewType.LOADING }?.id?.let { it + 1 }
                                ?: 0L
                        val appendList = pagingController.filterExclusive(result.books)
                            .mapIndexed { index, book ->
                                ViewItem.BookItem(offset + index, book)
                            }
                        bookList.value!!.filterNot { it.viewType == ViewType.LOADING } + appendList
                    } else {
                        pagingController.filterExclusive(result.books).mapIndexed { index, book ->
                            ViewItem.BookItem(index.toLong(), book)
                        }
                    }

                    if (pagingController.isLoading() && !pagingController.isLastPage()) {
                        list = list + loadingViewItem
                    }
                    bookList.postValue(list)
                    showLoading(false)
                }
            )
            .addTo(searchDisposables)
    }

    fun needNextPage(position: Int): Boolean {
        if (pagingController.isMaxLoading()) {
            return false
        }
        if (!pagingController.isLastPage()) {
            return position > bookList.value!!.size / 2
        }
        return pagingController.hasNextQuery()
    }

    fun cancelPendingPage() {
        searchDisposables.clear()
        pagingController.validQuery()?.also {
            pagingController.resetLoading(it)
        }
    }

    private fun showLoading(show: Boolean) {
        loading.postValue(show)
    }
}

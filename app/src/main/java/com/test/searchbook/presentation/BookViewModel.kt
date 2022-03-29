package com.test.searchbook.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.test.searchbook.data.api.model.Book
import com.test.searchbook.repository.BookRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class BookViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    companion object {
        private val TAG = BookViewModel::class.java.simpleName
    }

    @Inject
    lateinit var bookRepository: BookRepository

    private val searchDisposables = CompositeDisposable()
    private var pageInfo = PageInfo("")
    val bookTotalCount: MutableLiveData<Int> = MutableLiveData(0)
    val bookList: MutableLiveData<List<Book>> = MutableLiveData(listOf())

    override fun onCleared() {
        searchDisposables.clear()
    }

    fun searchNextPage(query: String) {
        if (pageInfo.key != query) {
            pageInfo = PageInfo("")
            searchDisposables.clear()
        } else if (pageInfo.isLastPage()) {
            return
        }
        val page = pageInfo.nextPage()
        Log.d(TAG, "query page:$page")
        pageInfo.loadProgress.incrementAndGet()
        bookRepository.searchBook(query, page.toString())
            .subscribeBy(
                onError = {
                    Log.e(TAG, "search error : ${it.message}")
                    it.printStackTrace()
                    pageInfo.loadProgress.decrementAndGet()
                },
                onSuccess = { result ->
                    val append = pageInfo.key == query
                    pageInfo.apply {
                        this.key = query
                        this.page = kotlin.runCatching {
                            result.page.toInt()
                        }.getOrDefault(0)
                        this.total = kotlin.runCatching {
                            result.total.toInt()
                        }.getOrDefault(0)
                        this.count += result.books.size
                        this.loadProgress.decrementAndGet()
                    }
                    Log.d(TAG, "total:${result.total}, page:${pageInfo.page}, count:${pageInfo.count}, size:${result.books.size}, loadProgress:${pageInfo.loadProgress.get()}")

                    if (append && bookList.value != null) {
                        bookList.postValue(bookList.value!! + result.books)
                    } else {
                        bookTotalCount.postValue(pageInfo.total)
                        bookList.postValue(result.books)
                    }
                }
            )
            .addTo(searchDisposables)
    }

    fun needNextPage(position: Int): Boolean {
        if (pageInfo.isLoading() || pageInfo.isLastPage()) {
            return false
        }
        return position < pageInfo.total - 1
    }
}

private data class PageInfo(var key: String, var page: Int = 0, var total: Int = 0) {
    companion object {
        const val MAX_LOAD_COUNT = 3
    }
    var queryPage = 0
    var count: Int = 0
    val loadProgress = AtomicInteger(0)

    fun nextPage(): Int {
        queryPage = queryPage.coerceAtLeast(page) + 1
        return queryPage
    }

    fun isLastPage(): Boolean {
        return count >= total
    }

    fun isLoading(): Boolean {
        return loadProgress.get() == MAX_LOAD_COUNT
    }
}
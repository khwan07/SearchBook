package com.test.searchbook.presentation

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.test.searchbook.R
import com.test.searchbook.data.api.model.SearchResult
import com.test.searchbook.presentation.search.PagingController
import com.test.searchbook.presentation.search.ViewItem
import com.test.searchbook.presentation.search.ViewType
import com.test.searchbook.repository.BookRepository
import javax.inject.Inject

class BookViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {
    companion object {
        private val TAG = BookViewModel::class.java.simpleName
    }

    @Inject
    lateinit var bookRepository: BookRepository

    private val context: Context
        get() = getApplication<Application>().applicationContext

    private val loadingViewItem = ViewItem.LoadingItem(Long.MAX_VALUE)
    private var pagingController = PagingController()
    val bookList: MutableLiveData<List<ViewItem>> = MutableLiveData(listOf())
    val error: MutableLiveData<Throwable> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val toast: MutableLiveData<String> = MutableLiveData()

    override fun onCleared() {
        bookRepository.cancelSearch()
    }

    fun searchNextPage(inputText: String) {
        if (pagingController.key != inputText) {
            pagingController = PagingController()
            pagingController.setQuery(inputText)
            bookRepository.cancelSearch()
            bookList.postValue(emptyList())
            showLoading(true)
        } else if (pagingController.isLastPage()) {
            return
        }

        val validQuery = pagingController.validQuery() ?: run {
            if (bookList.value!!.isEmpty()) {
                toast.postValue(context.getString(R.string.toast_no_result))
                showLoading(false)
            }
            return
        }
        val page = pagingController.nextPage(validQuery)

        pagingController.incrementLoading()
        bookRepository.searchBook(
            validQuery,
            page.toString(),
            object : BookRepository.ApiCallback<SearchResult> {
                override fun onSuccess(result: SearchResult) {
                    val append = pagingController.key == inputText

                    pagingController.key = inputText
                    pagingController.decrementLoading()
                    pagingController.setResult(result, validQuery)

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
                    bookList.value = list
                    showLoading(false)
                    if (pagingController.total == 0 && list.isEmpty()) {
                        toast.postValue(context.getString(R.string.toast_no_result))
                    }
                }

                override fun onFailure(t: Throwable) {
                    Log.e(TAG, "search error : ${t.message}")
                    t.printStackTrace()
                    cancelPendingLoad()
                    error.postValue(t)
                    showLoading(false)
                }
            })
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

    fun cancelPendingLoad() {
        bookRepository.cancelSearch()
        pagingController.validQuery()?.also {
            pagingController.resetLoading(it)
        }
    }

    private fun showLoading(show: Boolean) {
        loading.postValue(show)
    }
}

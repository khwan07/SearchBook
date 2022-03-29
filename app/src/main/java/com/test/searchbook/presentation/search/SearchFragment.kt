package com.test.searchbook.presentation.search

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.jakewharton.rxbinding4.view.clicks
import com.test.searchbook.databinding.FragmentSearchBinding
import com.test.searchbook.presentation.BookViewModel
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchFragment : DaggerFragment() {
    companion object {
        const val TAG = "SearchFragment"
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    @Inject
    lateinit var bookViewModel: BookViewModel

    @Inject
    lateinit var requestManager: RequestManager

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!
    private val compositeDisposable = CompositeDisposable()
    private var adapter: SearchListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUI()
        initViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }

    private fun initUI() {
        adapter = SearchListAdapter(requestManager)
        binding.bookList.itemAnimator = null
        binding.bookList.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.bookList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            val space = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10f,
                resources.displayMetrics
            ).toInt()

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                if (position < (parent.adapter?.itemCount ?: 0) - 1) {
                    outRect.bottom = space
                }
            }
        })
        binding.bookList.adapter = adapter

        binding.bookList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) {
                    return
                }
                val lastPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (bookViewModel.needNextPage(lastPosition)) {
                    Log.d(TAG, "needNextPage $lastPosition")
                    bookViewModel.searchNextPage("android")
                }
            }
        })

        binding.testBtn.clicks()
            .throttleFirst(100, TimeUnit.MILLISECONDS)
            .subscribe({
                bookViewModel.searchNextPage("android")
            }, Throwable::printStackTrace)
            .addTo(compositeDisposable)
    }

    private fun initViewModel() {
        bookViewModel.bookTotalCount.observe(viewLifecycleOwner) {
            adapter?.totalItemCount = it
        }
        bookViewModel.bookList.observe(viewLifecycleOwner) {
            adapter?.items = it
        }
    }
}
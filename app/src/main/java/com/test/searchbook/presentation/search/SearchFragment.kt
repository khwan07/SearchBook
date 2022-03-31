package com.test.searchbook.presentation.search

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import com.test.searchbook.R
import com.test.searchbook.databinding.FragmentSearchBinding
import com.test.searchbook.presentation.BookViewModel
import com.test.searchbook.presentation.detail.BookDetailFragment
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchFragment : DaggerFragment() {
    companion object {
        const val TAG = "SearchFragment"

        const val KEY_INPUT_TEXT = "key_input_text"

        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    @Inject
    lateinit var bookViewModel: BookViewModel

    private val requestManager: RequestManager by lazy { Glide.with(this) }
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

        savedInstanceState?.getString(KEY_INPUT_TEXT)?.also {
            if (bookViewModel.bookList.value.isNullOrEmpty()) {
                bookViewModel.searchNextPage(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_INPUT_TEXT, binding.editText.text.toString())
    }

    private fun initUI() {
        adapter = SearchListAdapter(requestManager).apply {
            click.map { it.adapterPosition }
                .subscribe({
                    val item = bookViewModel.bookList.value?.getOrNull(it) as? ViewItem.BookItem
                        ?: return@subscribe

                    bookViewModel.cancelPendingLoad()
                    hideKeyboard()

                    val fragment = BookDetailFragment.newInstance(item.data.isbn13)
                    childFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_from_right,
                            R.anim.slide_to_right,
                            R.anim.slide_from_right,
                            R.anim.slide_to_right
                        )
                        .replace(binding.fragmentArea.id, fragment, BookDetailFragment.TAG)
                        .addToBackStack(BookDetailFragment.TAG)
                        .setPrimaryNavigationFragment(fragment)
                        .commitAllowingStateLoss()

                }, Throwable::printStackTrace)
                .addTo(compositeDisposable)
        }
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
                    bookViewModel.searchNextPage(binding.editText.text.toString())
                }
            }
        })

        binding.searchView.isEnabled = false

        binding.editText.textChanges()
            .subscribe({
                binding.searchView.isEnabled = it.isNotEmpty()
            }, Throwable::printStackTrace)
            .addTo(compositeDisposable)

        binding.editText.setOnEditorActionListener { _, i, _ ->
            when (i) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    val text = binding.editText.text.toString()
                    if (text.isNotEmpty()) {
                        bookViewModel.searchNextPage(text)
                        hideKeyboard()
                    }
                    text.isNotEmpty()
                }
                else -> false
            }
        }

        binding.searchView.clicks()
            .throttleFirst(100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                bookViewModel.searchNextPage(binding.editText.text.toString())
                hideKeyboard()
            }, Throwable::printStackTrace)
            .addTo(compositeDisposable)
    }

    private fun initViewModel() {
        bookViewModel.bookList.observe(viewLifecycleOwner) {
            adapter?.items = it
        }

        bookViewModel.error.observe(viewLifecycleOwner) {
            when (it) {
                is UnknownHostException -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_network),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                }
            }
        }

        bookViewModel.toast.observe(viewLifecycleOwner) { str ->
            Toast.makeText(
                requireContext(),
                str,
                Toast.LENGTH_LONG
            ).show()
        }

        bookViewModel.loading.observe(viewLifecycleOwner) { show ->
            if (show) {
                binding.loading.visibility = View.VISIBLE
            } else {
                binding.loading.visibility = View.GONE
            }
        }
    }

    private fun hideKeyboard() {
        if (_binding == null) {
            return
        }
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)

        binding.editText.clearFocus()
    }
}
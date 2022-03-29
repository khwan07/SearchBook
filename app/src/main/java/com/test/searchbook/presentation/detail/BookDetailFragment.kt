package com.test.searchbook.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.test.searchbook.databinding.FragmentDetailBinding
import com.test.searchbook.presentation.BookDetailViewModel
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

class BookDetailFragment : DaggerFragment() {

    companion object {
        const val TAG = "BookDetailFragment"
        const val KEY_ISBN13 = "isbn13"

        fun newInstance(isbn13: String): BookDetailFragment {
            return BookDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_ISBN13, isbn13)
                }
            }
        }
    }

    @Inject
    lateinit var bookDetailViewModel: BookDetailViewModel

    @Inject
    lateinit var requestManager: RequestManager

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding!!
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isbn13 = (arguments ?: savedInstanceState)?.getString(KEY_ISBN13, "") ?: ""

        bookDetailViewModel.getBookDetail(isbn13)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    // TODO 에러처리
                    it.printStackTrace()
                },
                onComplete = {
                    // TODO 에러처리
                },
                onSuccess = {
                    requestManager.load(it.image)
                        .fitCenter()
                        .into(binding.image)

                    binding.title.text = it.title
                    binding.subtitle.text = it.subtitle
                }
            )
            .addTo(compositeDisposable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }
}
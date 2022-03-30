package com.test.searchbook.presentation.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.test.searchbook.R
import com.test.searchbook.databinding.FragmentDetailBinding
import com.test.searchbook.presentation.BookDetailViewModel
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.net.UnknownHostException
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
                    binding.author.text = it.authors
                    binding.publisher.text = " ${getString(R.string.dot)} ${it.publisher}"
                    binding.year.text = it.year
                    binding.price.text = it.price
                    binding.desc.text = it.desc
                    binding.url.apply {
                        if (it.url.isEmpty()) {
                            return@apply
                        }
                        val span = SpannableString(it.url)
                        span.setSpan(
                            object : ClickableSpan() {
                                override fun onClick(p0: View) {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
                                }
                            },
                            0, it.url.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        text = span
                        isClickable = true
                        movementMethod = LinkMovementMethod.getInstance()
                    }
                }
            )
            .addTo(compositeDisposable)

        bookDetailViewModel.error.observe(viewLifecycleOwner) {
            val message = when (it) {
                is UnknownHostException -> getString(R.string.error_network)
                else -> getString(R.string.error_unknown)
            }
            binding.image.visibility = View.GONE
            binding.textContainer.visibility = View.GONE

            binding.errorText.text = message
            binding.errorText.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }
}
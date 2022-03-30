package com.test.searchbook.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.jakewharton.rxbinding4.view.clicks
import com.test.searchbook.data.api.model.Book
import com.test.searchbook.databinding.VhSearchBinding
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject

class SearchViewHolder(private val binding: VhSearchBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun newInstance(parent: ViewGroup): SearchViewHolder {
            return SearchViewHolder(
                VhSearchBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    val click: Subject<RecyclerView.ViewHolder> = PublishSubject.create()

    init {
        binding.root.clicks().map { this@SearchViewHolder }.subscribe(click)
    }

    fun bind(book: Book, requestManager: RequestManager) {
        requestManager.load(book.image)
            .fitCenter()
            .into(binding.image)

        binding.title.text = book.title
        binding.subtitle.text = book.subtitle
        binding.price.text = book.price
    }
}
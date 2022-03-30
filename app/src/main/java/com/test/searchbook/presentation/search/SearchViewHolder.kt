package com.test.searchbook.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.test.searchbook.data.api.model.Book
import com.test.searchbook.databinding.VhSearchBinding

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

    var clickListener: ((RecyclerView.ViewHolder) -> Unit)? = null

    init {
        binding.root.setOnClickListener {
            clickListener?.invoke(this@SearchViewHolder)
        }
    }

    fun bind(book: Book, requestManager: RequestManager, id: Long) {
        requestManager.load(book.image)
            .fitCenter()
            .into(binding.image)

        binding.title.text = book.title
        binding.subtitle.text = book.subtitle
        binding.price.text = book.price
        binding.debugText.text = "$id"
    }
}
package com.test.searchbook.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.searchbook.databinding.VhSearchDummyBinding

class SearchDummyViewHolder(private val binding: VhSearchDummyBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun newInstance(parent: ViewGroup): SearchDummyViewHolder {
            return SearchDummyViewHolder(
                VhSearchDummyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    fun bind() {
        // TODO : animate dummy image alpha
    }
}
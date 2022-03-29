package com.test.searchbook.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.searchbook.databinding.VhSearchLoadingBinding

class SearchLoadingViewHolder(private val binding: VhSearchLoadingBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun newInstance(parent: ViewGroup): SearchLoadingViewHolder {
            return SearchLoadingViewHolder(
                VhSearchLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}
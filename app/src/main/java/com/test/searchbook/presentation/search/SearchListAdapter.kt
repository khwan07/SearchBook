package com.test.searchbook.presentation.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.test.searchbook.presentation.SimpleAdapter

class SearchListAdapter(private val requestManager: RequestManager) :
    SimpleAdapter<ViewItem, RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    var clickListener: ((RecyclerView.ViewHolder) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.BOOK.ordinal -> {
                SearchViewHolder.newInstance(parent)
                    .apply { this.clickListener = this@SearchListAdapter.clickListener }
            }
            else -> {
                SearchLoadingViewHolder.newInstance(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items?.getOrNull(position)
        when {
            item is ViewItem.BookItem && holder is SearchViewHolder -> {
                holder.bind(item.data, requestManager)
            }
            holder is SearchLoadingViewHolder -> {
                // no op.
            }
            else -> {
                throw IllegalStateException("not support view type")
            }
        }
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return items?.getOrNull(position)?.viewType?.ordinal ?: ViewType.LOADING.ordinal
    }

    override fun getItemId(position: Int): Long {
        return items?.getOrNull(position)?.id ?: RecyclerView.NO_ID
    }
}
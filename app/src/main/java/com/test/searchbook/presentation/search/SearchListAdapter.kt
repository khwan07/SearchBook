package com.test.searchbook.presentation.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.test.searchbook.data.api.model.Book
import com.test.searchbook.presentation.SimpleAdapter

class SearchListAdapter(private val requestManager: RequestManager) :
    SimpleAdapter<Book, RecyclerView.ViewHolder>() {

    var totalItemCount: Int? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.BOOK.ordinal -> SearchViewHolder.newInstance(parent)
            else -> SearchDummyViewHolder.newInstance(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items?.getOrNull(position)
        when {
            item != null && holder is SearchViewHolder -> {
                holder.bind(item, requestManager)
            }
            holder is SearchDummyViewHolder -> {
                holder.bind()
            }
            else -> {
                throw IllegalStateException("not support view type")
            }
        }
    }

    override fun getItemCount(): Int {
        return totalItemCount ?: items?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (items?.getOrNull(position) != null) {
            ViewType.BOOK.ordinal
        } else {
            ViewType.DUMMY.ordinal
        }
    }

    // TODO: id
    /*override fun getItemId(position: Int): Long {
        return items?.getOrNull(position)?.
    }*/
}

private enum class ViewType {
    BOOK, DUMMY
}
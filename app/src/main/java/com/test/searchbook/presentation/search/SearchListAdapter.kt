package com.test.searchbook.presentation.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.test.searchbook.presentation.SimpleAdapter
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject

class SearchListAdapter(private val requestManager: RequestManager) :
    SimpleAdapter<ViewItem, RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    val click: Subject<RecyclerView.ViewHolder> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.BOOK.ordinal -> {
                SearchViewHolder.newInstance(parent)
                    .apply { this.click.subscribe(this@SearchListAdapter.click) }
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
                holder.bind(item.data, requestManager, item.id)
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

    // TODO: id
    override fun getItemId(position: Int): Long {
        return items?.getOrNull(position)?.id ?: RecyclerView.NO_ID
    }
}
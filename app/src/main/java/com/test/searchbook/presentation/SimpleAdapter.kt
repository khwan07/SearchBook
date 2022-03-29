package com.test.searchbook.presentation

import androidx.recyclerview.widget.RecyclerView

abstract class SimpleAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    var items: List<T>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
}
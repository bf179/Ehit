package com.mitsuki.ehit.model.diff

import androidx.recyclerview.widget.DiffUtil
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.model.entity.db.SearchHistory

object Diff {
    val SEARCH_HISTORY by lazy {
        object : DiffUtil.ItemCallback<SearchHistory>() {
            override fun areItemsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean =
                oldItem === newItem

            override fun areContentsTheSame(
                oldItem: SearchHistory,
                newItem: SearchHistory
            ): Boolean = oldItem == newItem
        }
    }

    val QUICK_SEARCH = object : DiffUtil.ItemCallback<QuickSearch>() {
        override fun areItemsTheSame(oldItem: QuickSearch, newItem: QuickSearch): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(
            oldItem: QuickSearch,
            newItem: QuickSearch
        ): Boolean = oldItem == newItem
    }
}
package com.mitsuki.ehit.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.calculateDiff
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.db.QuickSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchShortcutAdapter : RecyclerView.Adapter<SearchShortcutAdapter.ViewHolder>() {

    var isEnable: Boolean = true
        set(value) {
            if (value != field) {
                if (value && !field) {
                    notifyItemRangeInserted(0, mData.size)
                } else if (!value && field) {
                    notifyItemRangeRemoved(0, mData.size)
                }
                field = value
            }
        }

    private val mData: MutableList<QuickSearch> = arrayListOf()

    private val currentItem: MutableLiveData<String> = MutableLiveData()

    private val mItemClick = { view: View ->
        val holder = view.tag as ViewHolder
        currentItem.postValue(mData[holder.bindingAdapterPosition].text)
    }

    val itemClickEvent: LiveData<String>
        get() = currentItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
        }
    }

    override fun getItemCount(): Int {
        return if (isEnable) mData.size else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, mData[position])
    }

    suspend fun submitData(data: List<QuickSearch>) {
        val result = withContext(Dispatchers.IO) {
            val result = calculateDiff(Diff.QUICK_SEARCH, mData, data)
            mData.clear()
            mData.addAll(data)
            result
        }

        withContext(Dispatchers.Main) {
            result.dispatchUpdatesTo(this@SearchShortcutAdapter)
        }
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        ) {

        private val mSearchIcon = view<ImageView>(R.id.search_item_icon)
        private val mSearchText = view<TextView>(R.id.search_item_text)

        fun bind(index: Int, item: QuickSearch) {
            mSearchIcon?.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
            mSearchText?.text = item.text
        }
    }
}
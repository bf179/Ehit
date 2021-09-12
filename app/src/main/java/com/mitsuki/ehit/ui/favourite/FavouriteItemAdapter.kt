package com.mitsuki.ehit.ui.favourite

import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.armory.adapter.notify.coroutine.NotifyQueueData
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.SingleLiveEvent
import com.mitsuki.ehit.crutch.extend.createItemView

import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ItemFavouriteOptionBinding
import com.mitsuki.ehit.model.ehparser.GalleryFavorites

class FavouriteItemAdapter(var checkedItemIndex: Int = 0) :
    RecyclerView.Adapter<FavouriteItemAdapter.ViewHolder>() {

    private val notifyQueueData: NotifyQueueData<Pair<String, Int>> by lazy {
        NotifyQueueData(GalleryFavorites.DIFF).apply {
            attachAdapter(this@FavouriteItemAdapter)
        }
    }

    val checkItem: SingleLiveEvent<Int> by lazy { SingleLiveEvent() }

    private val mItemCheckChange = { buttonView: CompoundButton, isChecked: Boolean ->
        if (buttonView.isPressed) {
            val position = (buttonView.tag as ViewHolder).bindingAdapterPosition
            if (isChecked && position != checkedItemIndex) {
                val olderChecked = checkedItemIndex
                checkedItemIndex = position
                notifyItemChanged(olderChecked)
                notifyItemChanged(checkedItemIndex)
                checkItem.postValue(checkedItemIndex)
            }
        }
    }

    override fun getItemCount(): Int = notifyQueueData.count

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            binding.favouriteOptionText.tag = this
            binding.favouriteOptionText.setOnCheckedChangeListener(mItemCheckChange)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(notifyQueueData.item(position)) {
            holder.binding.favouriteOptionText.text = first
            holder.binding.favouriteOptionText.isChecked = position == checkedItemIndex
            holder.binding.favouriteOptionCount.text = second.toString()
        }
    }

    fun postUpdate(lifecycle: Lifecycle, data: List<Pair<String, Int>>) {
        notifyQueueData.postUpdate(lifecycle, NotifyData.Refresh(data))
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_favourite_option)) {
        val binding by viewBinding(ItemFavouriteOptionBinding::bind)
    }
}

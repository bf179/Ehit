package com.mitsuki.ehit.ui.search

import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class QuickSearchItemTouchHelperCallback : ItemTouchHelper.Callback() {
    private val mSwapEvent: PublishSubject<Pair<Int, Int>> = PublishSubject.create()
    private val mDataSwap: PublishSubject<Pair<Int, Int>> = PublishSubject.create()

    val swapEvent: Observable<Pair<Int, Int>> get() =  mSwapEvent.hide()
    val dataSwap: Observable<Pair<Int, Int>> get() = mDataSwap.hide()

    private var lastSwap: Pair<Int, Int>? = null

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition
        lastSwap = fromPosition to toPosition
        mSwapEvent.onNext(lastSwap)
        return true
    }

    override fun isLongPressDragEnabled(): Boolean = false

    override fun isItemViewSwipeEnabled(): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                viewHolder?.itemView?.apply {
                    //为该tag设置0
                    //1、可防止内部实现修改自行设置的elevation
                    //2、不用再重写clearView来处理elevation，内部实现会在clearView方法中自动读取该tag
                    setTag(R.id.item_touch_helper_previous_elevation, 0f)
                    ViewCompat.setElevation(this, dp2px(8f))
                }
            }
            ItemTouchHelper.ACTION_STATE_IDLE -> {
                lastSwap?.apply { mDataSwap.onNext(this) }
                lastSwap = null
            }
        }
    }
}
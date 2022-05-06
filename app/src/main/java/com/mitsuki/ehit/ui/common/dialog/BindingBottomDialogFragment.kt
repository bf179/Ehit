package com.mitsuki.ehit.ui.common.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding

class BindingBottomDialogFragment<VB : ViewBinding>(
    @LayoutRes layout: Int,
    private val bind: (View) -> VB
) : BottomDialogFragment(layout) {

    lateinit var binding: VB
        private set

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
            ?.apply { binding = bind(this) }
    }

}
package com.mitsuki.ehit.mvvm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.mvvm.ui.adapter.GalleryDetailAdapter
import com.mitsuki.ehit.mvvm.viewmodel.MainViewModel
import com.mitsuki.mvvm.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_gallery_detail.*
import kotlinx.coroutines.launch

class GalleryDetailFragment : BaseFragment<MainViewModel>(R.layout.fragment_gallery_detail) {

    override val mViewModel: MainViewModel by activityViewModels()
    private val mAdapter by lazy { GalleryDetailAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.galleryDetail()?.observe(this@GalleryDetailFragment, Observer {
            mAdapter.submitData(lifecycle, it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        galleryDetailList.adapter = mAdapter
        galleryDetailList.layoutManager =
            GridLayoutManager(activity, 3).apply { spanSizeLookup = mAdapter.mSpanSizeLookup }
    }
}
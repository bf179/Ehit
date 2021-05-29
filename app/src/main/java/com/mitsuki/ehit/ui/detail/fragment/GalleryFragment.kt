package com.mitsuki.ehit.ui.detail.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Observer
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.OriginalSize
import com.mitsuki.armory.imagegesture.ImageGesture
import com.mitsuki.armory.imagegesture.StartType
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.crutch.Log
import com.mitsuki.ehit.crutch.extend.observe
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.FragmentGalleryBinding
import com.mitsuki.ehit.ui.detail.GalleryPreviewMenu
import com.mitsuki.ehit.ui.detail.widget.GalleryImageGesture
import com.mitsuki.ehit.ui.common.widget.OriginalTransformation
import com.mitsuki.ehit.viewmodel.GalleryViewModel
import com.mitsuki.loadprogress.Progress
import com.mitsuki.loadprogress.ProgressProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : BaseFragment(R.layout.fragment_gallery) {

    private val mViewModel: GalleryViewModel
            by createViewModelLazy(GalleryViewModel::class, { viewModelStore })

    private var mImageGesture: ImageGesture? = null

    private val binding by viewBinding(FragmentGalleryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.initData(arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mImageGesture =
            binding?.galleryImage?.run {
                GalleryImageGesture(this).apply {
                    startType = StartType.TOP
                    onLongPress = this@GalleryFragment::showGalleryMenu
                }
            }
        binding?.galleryIndex?.text = (mViewModel.index + 1).toString()

        mViewModel.data.observe(viewLifecycleOwner, Observer(this::onLoadImage))
        mViewModel.state.observe(viewLifecycleOwner, Observer(this::onViewState))
        ProgressProvider.event(mViewModel.tag)
            .observe(viewLifecycleOwner, this@GalleryFragment::onLoadProgress)

        mViewModel.obtainData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mImageGesture = null
    }

    private fun onViewState(state: GalleryViewModel.ViewState) {
        binding?.galleryLoading?.isVisible = state.loading
        binding?.galleryErrorMessage?.text = state.error ?: ""
    }

    private fun onLoadImage(url: String) {
        binding?.galleryImage?.load(url) {
            size(OriginalSize)
            transformations(OriginalTransformation())
            listener(
                onError = { _: ImageRequest, throwable: Throwable -> onLoadError(throwable) },
                onSuccess = { _: ImageRequest, _: ImageResult.Metadata -> onLoadSuccess() }
            )
        }
    }

    private fun onLoadError(throwable: Throwable) {
        binding?.galleryProgress?.isVisible = false
        binding?.galleryErrorMessage?.text = throwable.message
    }

    private fun onLoadSuccess() {
        binding?.galleryProgress?.isVisible = false
    }

    private fun onLoadProgress(progress: Progress?) {
        mViewModel.changeLoadingState(false)
        binding?.galleryProgress?.isVisible = true
        progress?.apply {
            binding?.galleryProgress?.progress =
                (currentBytes.toDouble() / contentLength.toDouble() * 100).toInt()
        }
    }


    private fun showGalleryMenu() {
        Log.debug("showGalleryMenu")
//        MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
//
//        }


        GalleryPreviewMenu().show(childFragmentManager, "menu")
    }
}
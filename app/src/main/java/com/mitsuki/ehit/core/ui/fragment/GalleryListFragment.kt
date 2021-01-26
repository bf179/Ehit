package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.mitsuki.armory.extend.toast
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.core.ui.adapter.*
import com.mitsuki.ehit.core.viewmodel.GalleryListViewModel
import com.mitsuki.ehit.core.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery_list.*
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GalleryListFragment : BaseFragment(R.layout.fragment_gallery_list) {

    private val mViewModel: GalleryListViewModel
            by createViewModelLazy(GalleryListViewModel::class, { viewModelStore })

    private val mMainViewModel: MainViewModel
            by createViewModelLazy(MainViewModel::class, { requireActivity().viewModelStore })

    private val mAdapter by lazy { GalleryAdapter() }
    private val mLoadAdapter by lazy { GalleryListLoadStateAdapter(mAdapter) }

    private val mConcatAdapter by lazy {
        val header = DefaultLoadStateAdapter(mAdapter)
        val footer = DefaultLoadStateAdapter(mAdapter)

        mAdapter.addLoadStateListener { loadStates ->
            header.loadState = loadStates.prepend
            footer.loadState = loadStates.append
        }
        ConcatAdapter(header, mLoadAdapter, mAdapter, footer)
    }

    private var isRefresh = false

    @Suppress("ControlFlowWithEmptyBody")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAdapter.currentItem.observe(this, Observer(this::toDetail))

        lifecycleScope.launchWhenCreated {
            mAdapter.loadStateFlow.collectLatest {
                isRefresh = it.refresh is LoadState.Loading
                mLoadAdapter.loadState = it.refresh
            }
        }

        mViewModel.galleryList.observe(this, Observer { mAdapter.submitData(lifecycle, it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }

        mMainViewModel.searchKey(hashCode()).observe(viewLifecycleOwner, Observer {
            //顶部显示相关搜索文字
            //并重新请求数据
            gallery_list?.topBar {
                findViewById<TextView>(R.id.top_search_text).text = it.showContent
            }

            mViewModel.galleryListPage(0)
            mViewModel.galleryListCondition(it)
            mAdapter.refresh()
        })

        //TODO：目前下拉刷新还存在问题等待排查，并且考虑是否增加一个初始加载错误页面
        gallery_list?.apply {
            //配置recycleView
            recyclerView {
                layoutManager = LinearLayoutManager(activity)
                adapter = mConcatAdapter
            }

            topBar {
                //TODO：topBar 展开效果不是非常好，搜索功能暂且稍后
                setOnClickListener {
                    Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                        .navigate(
                            R.id.action_gallery_list_fragment_to_search_fragment,
                            bundleOf(DataKey.GALLERY_FRAGMENT_CODE to this@GalleryListFragment.hashCode()),
                            null,
                            null
                        )
                }
            }

            setListener(
                extendControl = {
                    if (it) gallery_motion_layout?.transitionToStart()
                    else gallery_motion_layout?.transitionToEnd()
                }
            )
        }

        gallery_go_top?.setOnClickListener {
            gallery_list?.recyclerView()?.smoothScrollToPosition(0)
        }

        gallery_refresh?.setOnClickListener { mAdapter.refresh() }

        gallery_page_jump?.setOnClickListener { showPageJumpDialog() }
    }

    override fun onDestroy() {
        mMainViewModel.removeSearchKey(hashCode())
        super.onDestroy()
    }

    private fun toDetail(galleryClick: GalleryAdapter.GalleryClick) {
        with(galleryClick) {
            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                .navigate(
                    R.id.action_gallery_list_fragment_to_gallery_detail_fragment,
                    bundleOf(DataKey.GALLERY_INFO to data),
                    null,
                    null
                )
        }
    }

    private fun showPageJumpDialog() {
        MaterialDialog(requireContext()).show {
            input(inputType = InputType.TYPE_CLASS_NUMBER) { _, text ->
                mViewModel.galleryListPage(text.toString().toIntOrNull() ?: 0)
                mAdapter.refresh()
            }

            title(R.string.title_page_go_to)
            positiveButton(R.string.text_confirm)
            lifecycleOwner(this@GalleryListFragment)
        }
    }

}
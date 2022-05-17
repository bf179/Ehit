package com.mitsuki.ehit.ui.comment.activity

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.color
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityGalleryCommentBinding
import com.mitsuki.ehit.model.entity.Comment
import com.mitsuki.ehit.ui.comment.adapter.CommentLoadAdapter
import com.mitsuki.ehit.ui.comment.adapter.GalleryCommentAdapter
import com.mitsuki.ehit.viewmodel.GalleryCommentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryCommentActivity : BaseActivity() {

    private val mViewModel: GalleryCommentViewModel by viewModels()

    private val binding by viewBinding(ActivityGalleryCommentBinding::inflate)

    private val mInitAdapter by lazy { CommentLoadAdapter { mViewModel.loadComment(false) } }
    private val mMainAdapter by lazy { GalleryCommentAdapter() }

    //TODO 还缺一个empty adapter 一个显示全部adapter
    private val mAdapter by lazy { ConcatAdapter(mInitAdapter, mMainAdapter) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.topBar.topBarLayout.elevation = dp2px(4f)
        binding.topBar.topBarLayout.setBackgroundColor(color(R.color.background_color_general))
        binding.topBar.topBarBack.setOnClickListener { onBackPressed() }
        binding.topBar.topBarText.apply {
            text = intent?.getStringExtra(DataKey.GALLERY_NAME) ?: text(R.string.text_more_comments)
            ellipsize = TextUtils.TruncateAt.END
            isSingleLine = true
            setPadding(0, 0, dp2px(16F).toInt(), 0)
        }

        mViewModel.initData(intent)


        mMainAdapter.receiver<Pair<Int, Comment>>("VoteUp").observe(this) {
            mViewModel.voteComment(it.first, it.second, 1)
        }

        mMainAdapter.receiver<Pair<Int, Comment>>("VoteDown").observe(this) {
            mViewModel.voteComment(it.first, it.second, -1)
        }

        mViewModel.receiver<NotifyData<Comment>>("vote").observe(this) {
            mMainAdapter.updateData(lifecycle, it)
        }

        lifecycleScope.launchWhenCreated {
            mViewModel.loadStateFlow.collect {
                if (mInitAdapter.isOver) {
                    binding.commentRefresh.isRefreshing = it is LoadState.Loading
                } else {
                    mInitAdapter.loadState = it
                }
                binding.commentRefresh.isEnabled = mInitAdapter.isOver
            }
        }

        lifecycleScope.launchWhenCreated {
            mViewModel.commentDataFlow.collect { mMainAdapter.submitData(it) }
        }

        binding.commentRefresh.setOnRefreshListener {
            mViewModel.loadComment(false)
        }

        binding.commentList.apply {
            layoutManager = LinearLayoutManager(this@GalleryCommentActivity)
            adapter = mAdapter
        }

        mViewModel.loadComment(false)
    }
}
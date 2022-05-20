package com.mitsuki.ehit.ui.start.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BindingFragment
import com.mitsuki.ehit.crutch.OpenGate
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.databinding.FragmentLoginBinding
import com.mitsuki.ehit.ui.main.MainActivity
import com.mitsuki.ehit.ui.temp.adapter.*
import com.mitsuki.ehit.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 登录
 * cookie与账号密码登录合并
 * web登录用新activity
 */
@AndroidEntryPoint
class LoginFragment :
    BindingFragment<FragmentLoginBinding>(R.layout.fragment_login, FragmentLoginBinding::bind) {

    private val mViewModel: LoginViewModel
            by createViewModelLazy(LoginViewModel::class, { viewModelStore })

    private val mAccountAdapter by lazy { LoginAccountAdapter() }
    private val mCookieAdapter by lazy { LoginCookieAdapter() }
    private val mDomain by lazy { LoginDomain(memoryData) }
    private val mExtend by lazy { LoginExtend() }

    private val mAdapter by lazy {
        ConcatAdapter(
            mAccountAdapter,
            mCookieAdapter,
            mDomain,
            mExtend
        )
    }

    @Inject
    lateinit var openGate: OpenGate

    @Inject
    lateinit var shareData: ShareData

    @Inject
    lateinit var memoryData: MemoryData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.loginUi?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mViewModel.receiver<Int>("next").observe(viewLifecycleOwner) { nextNav() }

        mViewModel.receiver<String>("toast").observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }
        mExtend.receiver<Boolean>("switch").observe(viewLifecycleOwner) {
            mAccountAdapter.isEnable = !it
            mCookieAdapter.isEnable = it
        }

        mExtend.receiver<Int>("skip").observe(viewLifecycleOwner) { nextNav() }

        mAccountAdapter.receiver<LoginAccountAdapter.Account>("login").observe(viewLifecycleOwner) {
            mViewModel.login(it.account, it.password)
        }

        mCookieAdapter.receiver<LoginCookieAdapter.LoginCookie>("login")
            .observe(viewLifecycleOwner) { mViewModel.login(it.memberId, it.passHash, it.igneous) }
    }

    private fun nextNav() {
        shareData.spLoginShowed = true
        with(openGate.nextNav) {
            if (this == -1) {
                (requireActivity() as MainActivity).navDestination(R.id.nav_stack_main, null)
            } else {
                (requireActivity() as MainActivity).navigate(this)
            }
        }
    }
}
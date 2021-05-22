package com.mitsuki.ehit.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.extend.whiteStyle
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.model.entity.GalleryDetail
import com.mitsuki.ehit.ui.adapter.MoreInfoAdapter
import kotlinx.android.synthetic.main.activity_more_info.*
import kotlinx.android.synthetic.main.item_more_info.*
import kotlinx.android.synthetic.main.top_bar_normal_ver.*

class GalleryMoreInfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info)
        whiteStyle()


//
//        val infoAdapter = MoreInfoAdapter(info)
//
//        top_bar_back?.setOnClickListener { onBackPressed() }
//        more_info_value?.text = getText(R.string.text_more_information)
//
//        more_info_target?.apply {
//            layoutManager = LinearLayoutManager(this@GalleryMoreInfoActivity)
//            adapter = infoAdapter
//        }
    }
}
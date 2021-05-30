package com.mitsuki.ehit.model.page

import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.ehit.const.RequestKey
import java.lang.RuntimeException

class GalleryListPageIn(var pageSource: GalleryPageSource) : GeneralPageIn() {

    val targetUrl: String get() = pageSource.targetUrl
    val showContent: String get() = pageSource.showContent
    val type: GalleryPageSource.Type get() = pageSource.type

    fun addPage(source: UrlParams, index: Int) {
        if (index == START || !pageSource.hasPaging) return
        source.urlParams(RequestKey.PAGE, index.toString())
    }

    fun addSearchKey(source: UrlParams) {
        pageSource.applyKey(source)
    }

    fun docerPrevKey(key: Int?): Int? {
        if (!pageSource.hasPaging) return null
        return key
    }

    fun docerNextKey(key: Int?): Int? {
        if (!pageSource.hasPaging) return null
        return key
    }
}
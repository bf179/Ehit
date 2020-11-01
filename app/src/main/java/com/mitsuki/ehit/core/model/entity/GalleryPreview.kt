package com.mitsuki.ehit.core.model.entity

import com.mitsuki.ehit.being.exception.ParseException
import com.mitsuki.ehit.core.model.ehparser.Matcher
import com.mitsuki.ehit.core.model.ehparser.htmlEscape
import java.util.regex.Pattern

data class GalleryPreview(val imageUrl: String, val reloadKey: String, val downloadUrl: String) {

    companion object {
        fun parse(content: String?): GalleryPreview {
            if (content.isNullOrEmpty()) throw ParseException("未请求到数据")
            val imageUrl: String = Matcher.PREVIEW_IMG_URL.dataParse(content, "not found image url")
            val reloadKey: String =
                Matcher.PREVIEW_RELOAD_KEY.dataParse(content, "not found reload key")
//            val downloadUrl: String =
//                Matcher.PREVIEW_DOWNLOAD_URL.dataParse(content, "not found download url")
            return GalleryPreview(imageUrl, reloadKey, "")
        }

        private fun Pattern.dataParse(content: String, msg: String = ""): String {
            return matcher(content).run {
                if (find()) {
                    group(1)?.htmlEscape() ?: throw ParseException(msg)
                } else {
                    throw ParseException(msg)
                }
            }
        }
    }
}
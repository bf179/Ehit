package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.mitsuki.ehit.const.DBKey


@Entity(
    tableName = DBKey.TABLE_GALLERY_INFO,
    primaryKeys = ["gid", "token"]
)
data class GalleryInfoCache(
    @ColumnInfo(name = "gid") val gid: Long,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "api_uid") val apiUID: String,
    @ColumnInfo(name = "api_key") val apiKey: String,
    @ColumnInfo(name = "torrent_url") val torrentUrl: String = "",
    @ColumnInfo(name = "torrent_count") val torrentCount: Int = 0,
    @ColumnInfo(name = "archive_url") val archiveUrl: String = "",
    @ColumnInfo(name = "detail_thumb") val detailThumb: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "title_jp") val titleJP: String,
    @ColumnInfo(name = "category") val category: String = "Unknow",
    @ColumnInfo(name = "uploader") val uploader: String,
    @ColumnInfo(name = "posted") val posted: String,
    @ColumnInfo(name = "parent") val parent: String,
    @ColumnInfo(name = "visible") val visible: String,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "size") val size: String,
    @ColumnInfo(name = "pages_str") val pagesStr: String,
    @ColumnInfo(name = "favorite") val favorite: String,
    @ColumnInfo(name = "rating_count") val ratingCount: Int,
    @ColumnInfo(name = "rating") val rating: Float,
    @ColumnInfo(name = "favorite_name") val favoriteName: String? = null,
    @ColumnInfo(name = "preview_pages") val previewPages: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is GalleryInfoCache &&
                gid == other.gid &&
                token == other.token &&
                apiUID == other.apiUID &&
                apiKey == other.apiKey &&
                torrentUrl == other.torrentUrl &&
                torrentCount == other.torrentCount &&
                archiveUrl == other.archiveUrl &&
                detailThumb == other.detailThumb &&
                title == other.title &&
                titleJP == other.titleJP &&
                category == other.category &&
                uploader == other.uploader &&
                posted == other.posted &&
                parent == other.parent &&
                visible == other.visible &&
                language == other.language &&
                size == other.size &&
                pagesStr == other.pagesStr &&
                favorite == other.favorite &&
                ratingCount == other.ratingCount &&
                rating == other.rating &&
                favoriteName == other.favoriteName &&
                previewPages == other.previewPages
    }

    override fun hashCode(): Int {
        var result = gid.hashCode()
        result = 31 * result + token.hashCode()
        result = 31 * result + apiUID.hashCode()
        result = 31 * result + apiKey.hashCode()
        result = 31 * result + torrentUrl.hashCode()
        result = 31 * result + torrentCount
        result = 31 * result + archiveUrl.hashCode()
        result = 31 * result + detailThumb.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + titleJP.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + uploader.hashCode()
        result = 31 * result + posted.hashCode()
        result = 31 * result + parent.hashCode()
        result = 31 * result + visible.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + pagesStr.hashCode()
        result = 31 * result + favorite.hashCode()
        result = 31 * result + ratingCount
        result = 31 * result + rating.hashCode()
        result = 31 * result + (favoriteName?.hashCode() ?: 0)
        result = 31 * result + previewPages
        return result
    }
}
package com.mitsuki.ehit.core.model.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.armory.httprookie.convert.StringConvert
import com.mitsuki.armory.httprookie.request.header
import com.mitsuki.armory.httprookie.request.json
import com.mitsuki.armory.httprookie.request.params
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.being.MemoryCache
import com.mitsuki.ehit.being.network.RequestResult
import com.mitsuki.ehit.being.network.Url
import com.mitsuki.ehit.being.toJson
import com.mitsuki.ehit.const.ParaValue
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.core.crutch.PageIn
import com.mitsuki.ehit.core.model.convert.GalleryPreviewConvert
import com.mitsuki.ehit.core.model.convert.ImageSourceConvert
import com.mitsuki.ehit.core.model.convert.LoginConvert
import com.mitsuki.ehit.core.model.convert.RateBackConvert
import com.mitsuki.ehit.core.model.entity.*
import com.mitsuki.ehit.core.model.pagingsource.GalleryDetailSource
import com.mitsuki.ehit.core.model.pagingsource.GalleryListSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.ceil

class RemoteRepositoryImpl @Inject constructor() : Repository {

    private val mListPagingConfig =
        PagingConfig(pageSize = 25)

    private val mDetailPagingConfig =
        PagingConfig(pageSize = 40)

    override fun galleryList(pageIn: PageIn): Flow<PagingData<Gallery>> {
        Log.e("RemoteRepositoryImpl", "galleryList")
        return Pager(mListPagingConfig, initialKey = 0) {
            GalleryListSource(pageIn)
        }.flow
    }

    override fun galleryDetail(
        gid: Long,
        token: String,
        pageIn: PageIn,
        detailSource: GalleryDetailWrap
    ): Flow<PagingData<ImageSource>> {
        return Pager(mDetailPagingConfig, initialKey = 0) {
            GalleryDetailSource(gid, token, pageIn, detailSource)
        }.flow
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun galleryPreview(
        gid: Long,
        token: String,
        index: Int
    ): RequestResult<GalleryPreview> {
        return withContext(Dispatchers.IO) {

            val data = MemoryCache.getImagePage(gid, index)
            if (data != null) {
                RequestResult.SuccessResult(data)
            } else {
                val remoteData: Response<GalleryPreview> = HttpRookie
                    .get<GalleryPreview>(Url.galleryPreviewDetail(gid, token, index)) {
                        convert = GalleryPreviewConvert()
                    }
                    .execute()

                try {
                    when (remoteData) {
                        is Response.Success<GalleryPreview> -> RequestResult.SuccessResult(
                            remoteData.requireBody()
                                .apply { MemoryCache.cacheImagePage(gid, index, this) }
                        )
                        is Response.Fail<*> -> throw remoteData.throwable
                    }
                } catch (inner: Throwable) {
                    RequestResult.FailResult<GalleryPreview>(inner)
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun galleryDetailWithPToken(
        gid: Long,
        token: String,
        index: Int
    ): RequestResult<String> {
        return withContext(Dispatchers.IO) {

            val remoteData = HttpRookie
                .get<PageInfo<ImageSource>>(Url.galleryDetail(gid, token)) {
                    convert = ImageSourceConvert()
                    if (index != 0) urlParams(RequestKey.PAGE_DETAIL to index.toString())
                }
                .execute()

            try {
                when (remoteData) {
                    is Response.Success<PageInfo<ImageSource>> -> {
                        val pToken = remoteData.requireBody().run {
                            MemoryCache.cacheImageToken(gid, data)
                            MemoryCache.getImageToken(gid, index)
                        } ?: throw Exception("not found pToken")

                        RequestResult.SuccessResult(pToken)
                    }
                    is Response.Fail<*> -> throw remoteData.throwable
                }
            } catch (inner: Throwable) {
                RequestResult.FailResult<String>(inner)
            }
        }
    }

    override suspend fun login(account: String, password: String): RequestResult<String> {
        return withContext(Dispatchers.IO) {
            val loginData = HttpRookie
                .post<String>(Url.login) {
                    convert = LoginConvert()
                    params(RequestKey.REFERER to ParaValue.LOGIN_REFERER)
                    params(RequestKey.B to "")
                    params(RequestKey.BT to "")

                    params(RequestKey.USER_NAME to account)
                    params(RequestKey.PASS_WORD to password)
                    params(RequestKey.COOKIE_DATE to "1")
                    //params(RequestKey.PRIVACY to "1")

                    header(RequestKey.HEADER_ORIGIN to ParaValue.LOGIN_HEADER_ORIGIN)
                    header(RequestKey.HEADER_REFERER to ParaValue.LOGIN_HEADER_REFERER)
                }
                .execute()
            try {
                when (loginData) {
                    is Response.Success<String> -> RequestResult.SuccessResult(loginData.requireBody())
                    is Response.Fail<*> -> throw loginData.throwable
                }
            } catch (inner: Throwable) {
                RequestResult.FailResult(inner)
            }
        }
    }


    override suspend fun rating(detail: GalleryDetail, rating: Float): RequestResult<RateBack> {
        return withContext(Dispatchers.IO) {
            val data = HttpRookie
                .post<RateBack>(Url.api) {
                    convert = RateBackConvert()
                    json(
                        RequestRateInfo(
                            apiUid = detail.apiUID,
                            apiKey = detail.apiKey,
                            galleryID = detail.gid.toString(),
                            token = detail.token,
                            rating = ceil(rating * 2).toInt()
                        ).toJson()
                    )
                    header(RequestKey.HEADER_ORIGIN to Url.currentDomain)
                    header(RequestKey.HEADER_REFERER to Url.galleryDetail(detail.gid, detail.token))
                }
                .execute()
            try {
                when (data) {
                    is Response.Success<RateBack> -> RequestResult.SuccessResult(data.requireBody())
                    is Response.Fail<*> -> throw data.throwable
                }
            } catch (inner: Throwable) {
                RequestResult.FailResult(inner)
            }
        }


    }
}
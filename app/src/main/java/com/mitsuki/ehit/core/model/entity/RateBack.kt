package com.mitsuki.ehit.core.model.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RateBack(
    @Json(name = "rating_avg") val avg: Double,
    @Json(name = "rating_cnt") val count: Int
)
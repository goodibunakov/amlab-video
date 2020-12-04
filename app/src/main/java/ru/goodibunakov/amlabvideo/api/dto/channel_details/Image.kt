package ru.goodibunakov.amlabvideo.api.dto.channel_details

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("bannerExternalUrl")
    val bannerExternalUrl: String
)
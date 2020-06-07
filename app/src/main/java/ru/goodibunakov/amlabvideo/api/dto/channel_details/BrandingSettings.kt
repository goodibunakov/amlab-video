package ru.goodibunakov.amlabvideo.api.dto.channel_details

import com.google.gson.annotations.SerializedName

data class BrandingSettings(
        @SerializedName("channel")
        val channel: Channel,
        @SerializedName("image")
        val image: Image
)
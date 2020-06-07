package ru.goodibunakov.amlabvideo.api.dto.channel_details

import com.google.gson.annotations.SerializedName

data class Item(
        @SerializedName("brandingSettings")
        val brandingSettings: BrandingSettings
)
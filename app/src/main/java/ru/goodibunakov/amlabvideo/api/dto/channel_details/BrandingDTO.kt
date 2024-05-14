package ru.goodibunakov.amlabvideo.api.dto.channel_details

import com.google.gson.annotations.SerializedName

data class BrandingDTO(
    @SerializedName("items")
    val items: List<Item>
)
package ru.goodibunakov.amlabvideo.api.dto.common

import com.google.gson.annotations.SerializedName

data class High(
        @SerializedName("height")
        val height: Int,
        @SerializedName("url")
        val url: String,
        @SerializedName("width")
        val width: Int
)
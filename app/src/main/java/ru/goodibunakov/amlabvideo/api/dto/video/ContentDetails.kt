package ru.goodibunakov.amlabvideo.api.dto.video

import com.google.gson.annotations.SerializedName

data class ContentDetails(
        @SerializedName("videoId")
        val videoId: String,
        @SerializedName("videoPublishedAt")
        val videoPublishedAt: String
)
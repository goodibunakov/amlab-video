package ru.goodibunakov.amlabvideo.api.dto.videos

import com.google.gson.annotations.SerializedName
import ru.goodibunakov.amlabvideo.api.dto.videos.Default
import ru.goodibunakov.amlabvideo.api.dto.videos.High
import ru.goodibunakov.amlabvideo.api.dto.videos.Medium

data class Thumbnails(
        @SerializedName("default")
        val default: Default,
        @SerializedName("high")
        val high: High,
        @SerializedName("medium")
        val medium: Medium
)
package ru.goodibunakov.amlabvideo.api.dto.videos_all

import com.google.gson.annotations.SerializedName
import ru.goodibunakov.amlabvideo.api.dto.common.Default
import ru.goodibunakov.amlabvideo.api.dto.common.High
import ru.goodibunakov.amlabvideo.api.dto.common.Medium

data class Thumbnails(
    @SerializedName("default")
    val default: Default,
    @SerializedName("high")
    val high: High,
    @SerializedName("medium")
    val medium: Medium
)
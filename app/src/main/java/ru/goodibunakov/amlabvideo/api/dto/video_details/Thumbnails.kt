package ru.goodibunakov.amlabvideo.api.dto.video_details

import com.google.gson.annotations.SerializedName
import ru.goodibunakov.amlabvideo.api.dto.common.Default
import ru.goodibunakov.amlabvideo.api.dto.common.High
import ru.goodibunakov.amlabvideo.api.dto.common.Maxres
import ru.goodibunakov.amlabvideo.api.dto.common.Medium

data class Thumbnails(
        @SerializedName("default")
        val default: Default,
        @SerializedName("high")
        val high: High,
        @SerializedName("maxres")
        val maxres: Maxres,
        @SerializedName("medium")
        val medium: Medium,
        @SerializedName("standard")
        val standard: Standard
)
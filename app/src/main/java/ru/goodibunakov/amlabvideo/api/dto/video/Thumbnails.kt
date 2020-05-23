package ru.goodibunakov.amlabvideo.api.dto.video

import com.google.gson.annotations.SerializedName

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
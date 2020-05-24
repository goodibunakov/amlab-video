package ru.goodibunakov.amlabvideo.api.dto.videos_all

import com.google.gson.annotations.SerializedName

data class Id(
        @SerializedName("kind")
        val kind: String,
        @SerializedName("playlistId")
        val playlistId: String,
        @SerializedName("videoId")
        val videoId: String
)
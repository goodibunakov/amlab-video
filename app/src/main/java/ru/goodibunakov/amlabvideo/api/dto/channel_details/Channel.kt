package ru.goodibunakov.amlabvideo.api.dto.channel_details

import com.google.gson.annotations.SerializedName

data class Channel(
        @SerializedName("description")
        val description: String,
        @SerializedName("title")
        val title: String
)
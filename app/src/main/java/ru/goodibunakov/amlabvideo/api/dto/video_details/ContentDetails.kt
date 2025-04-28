package ru.goodibunakov.amlabvideo.api.dto.video_details

import com.google.gson.annotations.SerializedName

data class ContentDetails(
    @SerializedName("caption")
    val caption: String,
    @SerializedName("definition")
    val definition: String,
    @SerializedName("dimension")
    val dimension: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("licensedContent")
    val licensedContent: Boolean,
    @SerializedName("projection")
    val projection: String
)
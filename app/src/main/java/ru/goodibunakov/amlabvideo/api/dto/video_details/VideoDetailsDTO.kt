package ru.goodibunakov.amlabvideo.api.dto.video_details

import com.google.gson.annotations.SerializedName

data class VideoDetailsDTO(
    @SerializedName("etag")
    val etag: String,
    @SerializedName("items")
    val items: List<Item>,
    @SerializedName("kind")
    val kind: String,
    @SerializedName("pageInfo")
    val pageInfo: PageInfo
)
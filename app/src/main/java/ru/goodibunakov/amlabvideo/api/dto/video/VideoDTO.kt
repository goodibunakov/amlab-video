package ru.goodibunakov.amlabvideo.api.dto.video

import com.google.gson.annotations.SerializedName

data class VideoDTO(
        @SerializedName("etag")
        val etag: String,
        @SerializedName("items")
        val items: List<Item>,
        @SerializedName("kind")
        val kind: String,
        @SerializedName("nextPageToken")
        val nextPageToken: String,
        @SerializedName("pageInfo")
        val pageInfo: PageInfo
)
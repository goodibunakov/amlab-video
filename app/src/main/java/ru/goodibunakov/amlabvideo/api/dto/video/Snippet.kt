package ru.goodibunakov.amlabvideo.api.dto.video

import com.google.gson.annotations.SerializedName

data class Snippet(
    @SerializedName("channelId")
    val channelId: String,
    @SerializedName("channelTitle")
    val channelTitle: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("playlistId")
    val playlistId: String,
    @SerializedName("position")
    val position: Int,
    @SerializedName("publishedAt")
    val publishedAt: String,
    @SerializedName("resourceId")
    val resourceId: ResourceId,
    @SerializedName("thumbnails")
    val thumbnails: Thumbnails?,
    @SerializedName("title")
    val title: String
)
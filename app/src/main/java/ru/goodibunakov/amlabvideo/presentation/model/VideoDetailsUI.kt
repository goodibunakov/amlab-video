package ru.goodibunakov.amlabvideo.presentation.model

import java.util.*

data class VideoDetailsUI(
    val viewCount: String,
    val likeCount: String,
    val commentCount: String,
    val favoriteCount: String,
    val dislikeCount: String,
    val title: String,
    val description: String,
    val publishedAt: String,
    val publishedAtDate: Date,
    val tags: List<String>?,
    val categoryId: String,
    val liveBroadcastContent: String,
    val duration: String,
    val thumbnail: String
)
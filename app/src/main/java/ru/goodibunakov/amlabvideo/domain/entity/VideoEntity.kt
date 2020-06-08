package ru.goodibunakov.amlabvideo.domain.entity

data class VideoEntity (
        val title: String,
        val videoId: String,
        val imageUrl: String?,
        val createdDate: String
)
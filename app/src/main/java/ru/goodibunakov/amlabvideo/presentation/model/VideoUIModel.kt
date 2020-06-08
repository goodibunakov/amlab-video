package ru.goodibunakov.amlabvideo.presentation.model

data class VideoUIModel(
        val title: String,
        val videoId: String,
        val imageUrl: String?,
        val createdDate: String
)
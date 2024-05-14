package ru.goodibunakov.amlabvideo.presentation.model


data class MessageUIItem(
    val id: Int,
    val title: String,
    val body: String,
    val image: String,
    val launchURL: String,
    val dateReceived: Long
)
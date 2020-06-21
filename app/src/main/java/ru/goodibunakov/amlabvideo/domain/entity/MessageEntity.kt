package ru.goodibunakov.amlabvideo.domain.entity

data class MessageEntity (
        val id: Int,
        val title: String,
        val body: String,
        val image: String,
        val launchURL: String,
        val dateReceived: Long
)
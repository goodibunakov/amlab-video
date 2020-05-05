package ru.goodibunakov.amlabvideo.domain

interface Mapper<in From, out To> {
    fun map(from: From): To
}
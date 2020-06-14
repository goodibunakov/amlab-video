package ru.goodibunakov.amlabvideo.api.dto.error

import com.google.gson.annotations.SerializedName

data class ErrorItem(
        @SerializedName("domain")
        val domain: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("reason")
        val reason: String
)
package ru.goodibunakov.amlabvideo.api.dto.error

import com.google.gson.annotations.SerializedName

data class Error(
        @SerializedName("code")
        val code: Int,
        @SerializedName("errors")
        val errors: List<ErrorItem>,
        @SerializedName("message")
        val message: String
)
package ru.goodibunakov.amlabvideo.api.dto.error

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuotaErrorResponse(
        @SerializedName("error")
        val error: Error
) : Parcelable {

    @Parcelize
    data class Error(
            @SerializedName("code")
            val code: Int,
            @SerializedName("errors")
            val errors: List<ErrorItem>,
            @SerializedName("message")
            val message: String
    ) : Parcelable

    @Parcelize
    data class ErrorItem(
            @SerializedName("domain")
            val domain: String,
            @SerializedName("message")
            val message: String,
            @SerializedName("reason")
            val reason: String
    ) : Parcelable
}
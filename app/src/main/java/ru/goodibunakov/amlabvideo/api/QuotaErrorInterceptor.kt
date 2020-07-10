package ru.goodibunakov.amlabvideo.api

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Interceptor
import okhttp3.Response
import ru.goodibunakov.amlabvideo.api.dto.error.QuotaErrorResponse


/**
 * {
"error": {
"errors": [
{
"domain": "youtube.quota",
"reason": "quotaExceeded",
"message": "The request cannot be completed because you have exceeded your \u003ca href=\"/youtube/v3/getting-started#quota\"\u003equota\u003c/a\u003e."
}
],
"code": 403,
"message": "The request cannot be completed because you have exceeded your \u003ca href=\"/youtube/v3/getting-started#quota\"\u003equota\u003c/a\u003e."
}
}
 */

class QuotaErrorInterceptor(
        private val broadcastManager: LocalBroadcastManager,
        val gson: Gson
) : Interceptor {

    companion object {
        const val REASON_QUOTA_EXCEEDED = "quotaExceeded"
        const val REASON_DAILY_LIMIT_EXCEEDED = "dailyLimitExceeded"
        const val ACTION_QUOTA_EXCEEDED = "ru.goodibunakov.amlabvideo.ACTION_QUOTA_EXCEEDED"
        const val EXTRA_QUOTA_EXCEEDED = "reason"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == 403) {
            var quotaErrorResponse: QuotaErrorResponse? = null
            val responseString = response.body?.string()
            return try {
                quotaErrorResponse = gson.fromJson(responseString, QuotaErrorResponse::class.java)
                Log.d("debug", "QuotaErrorInterceptor intercept quotaErrorResponse = $quotaErrorResponse")
                if (quotaErrorResponse != null
                        && (quotaErrorResponse.error.errors[0].reason == REASON_QUOTA_EXCEEDED
                                || quotaErrorResponse.error.errors[0].reason == REASON_DAILY_LIMIT_EXCEEDED)) {
                    Log.d("debug", "QuotaErrorInterceptor intercept sendBroadcast")
                    val intent = Intent(ACTION_QUOTA_EXCEEDED)
                            .apply { putExtra(EXTRA_QUOTA_EXCEEDED, quotaErrorResponse) }


                    val a = broadcastManager.sendBroadcast(intent)
                    Log.d("debug", "QuotaErrorInterceptor intercept intent = $intent")
                    Log.d("debug", "QuotaErrorInterceptor intercept send = $a")
                }
                response
            } catch (error: JsonSyntaxException) {
                response
            }
        }

        return response
    }
}
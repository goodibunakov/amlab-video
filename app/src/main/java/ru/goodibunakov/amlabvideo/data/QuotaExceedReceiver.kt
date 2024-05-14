package ru.goodibunakov.amlabvideo.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.goodibunakov.amlabvideo.api.QuotaErrorInterceptor.Companion.EXTRA_QUOTA_EXCEEDED
import ru.goodibunakov.amlabvideo.api.dto.error.QuotaErrorResponse

class QuotaExceedReceiver(private val errorQuotaLiveData: MutableLiveData<Boolean>) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("debug", "QuotaExceedReceiver onReceive intent = $intent")
        if (intent != null && intent.getParcelableExtra<QuotaErrorResponse>(EXTRA_QUOTA_EXCEEDED) is QuotaErrorResponse) {
            errorQuotaLiveData.value = true
        }
    }
}
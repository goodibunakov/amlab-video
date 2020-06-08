package ru.goodibunakov.amlabvideo.api

import okhttp3.Interceptor
import okhttp3.Response


class QuotaErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == 403) {

            return response
        }

        return response
    }
}
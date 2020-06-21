package ru.goodibunakov.amlabvideo.data

import android.content.Context
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal

class NotificationOpenedHandler(val context: Context) : OneSignal.NotificationOpenedHandler {

    override fun notificationOpened(result: OSNotificationOpenResult?) {

    }
}
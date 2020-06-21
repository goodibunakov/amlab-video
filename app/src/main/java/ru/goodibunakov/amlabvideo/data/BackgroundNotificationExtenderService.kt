package ru.goodibunakov.amlabvideo.data

import com.onesignal.NotificationExtenderService
import com.onesignal.OSNotificationReceivedResult

class BackgroundNotificationExtenderService : NotificationExtenderService() {

    override fun onNotificationProcessing(notification: OSNotificationReceivedResult?): Boolean = false
}
package ru.goodibunakov.amlabvideo.data.notifications

import android.content.Context
import android.content.Intent
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OneSignal
import ru.goodibunakov.amlabvideo.presentation.activity.MainActivity

class NotificationOpenedHandler(val context: Context) : OneSignal.OSNotificationOpenedHandler {

    companion object {
        const val INTENT_FROM_NOTIFICATION = "from_notification"
    }

    override fun notificationOpened(result: OSNotificationOpenedResult?) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(INTENT_FROM_NOTIFICATION, true)
        }
        context.startActivity(intent)
    }
}
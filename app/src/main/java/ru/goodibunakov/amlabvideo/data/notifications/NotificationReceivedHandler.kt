package ru.goodibunakov.amlabvideo.data.notifications

import android.content.Context
import android.util.Log
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.data.model.MessageItem


class NotificationReceivedHandler : OneSignal.OSRemoteNotificationReceivedHandler {

    private lateinit var disposable: Disposable

    override fun remoteNotificationReceived(context: Context?, notificationReceivedEvent: OSNotificationReceivedEvent?) {
        if (notificationReceivedEvent?.notification?.rawPayload == null) return
        Log.d("debug", "NotificationReceivedHandler notification = ${notificationReceivedEvent.notification}")

        val data = notificationReceivedEvent.notification
        data.let {
            val message = MessageItem(
                title = data.title,
                body = data.body,
                image = data.bigPicture ?: "",
                launchURL = data.launchURL ?: "",
                dateReceived = System.currentTimeMillis()
            )

            val saveNotificationUseCase = (context as? AmlabApplication)?.getSaveNotificationUseCase()
            saveNotificationUseCase?.let {
                it.set(message)
                disposable = it.buildObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.d("debug", "notification saved to db = $it")
                    }, {
                        Log.d("debug", "notification error = $it")
                    })
            }
        }
    }
}
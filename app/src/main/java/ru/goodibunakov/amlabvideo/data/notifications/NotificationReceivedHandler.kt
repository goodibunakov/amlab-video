package ru.goodibunakov.amlabvideo.data.notifications

import android.util.Log
import com.onesignal.OSNotification
import com.onesignal.OneSignal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.data.model.MessageItem
import ru.goodibunakov.amlabvideo.domain.usecase.SaveNotificationUseCase


class NotificationReceivedHandler(
        private val saveNotificationUseCase: SaveNotificationUseCase
) : OneSignal.NotificationReceivedHandler {

    private lateinit var disposable: Disposable

    override fun notificationReceived(notification: OSNotification?) {
        Log.d("debug", "NotificationReceivedHandler notification = ${notification?.payload}")

        val data = notification!!.payload
        data?.let {
            val message = MessageItem(
                    title = data.title,
                    body = data.body,
                    image = data.bigPicture ?: "",
                    launchURL = data.launchURL ?: "",
                    dateReceived = System.currentTimeMillis()
            )

            saveNotificationUseCase.set(message)
            disposable = saveNotificationUseCase.buildObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.d("debug", "notification saved to db = $it")
                    }, {
                        Log.d("debug", "notification error = $it")
                    })

        }
    }

    private fun disposeThis() {
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
    }
}
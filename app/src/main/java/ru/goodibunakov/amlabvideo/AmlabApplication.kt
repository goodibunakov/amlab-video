package ru.goodibunakov.amlabvideo

import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.onesignal.OneSignal
import ru.goodibunakov.amlabvideo.api.ApiService
import ru.goodibunakov.amlabvideo.data.NotificationOpenedHandler
import ru.goodibunakov.amlabvideo.data.NotificationReceivedHandler
import ru.goodibunakov.amlabvideo.data.database.AmlabDatabase
import ru.goodibunakov.amlabvideo.data.repositories.ApiRepositoryImpl
import ru.goodibunakov.amlabvideo.data.repositories.DatabaseRepositoryImpl
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import ru.goodibunakov.amlabvideo.domain.usecase.*
import ru.goodibunakov.amlabvideo.presentation.viewmodels.ViewModelFactory

class AmlabApplication : MultiDexApplication() {

    companion object {
        //        lateinit var fileRepository: FileRepository
        lateinit var apiRepository: ApiRepository
        lateinit var databaseRepository: DatabaseRepository
        lateinit var viewModelFactory: ViewModelFactory
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Stetho.initializeWithDefaults(this)

        apiRepository = ApiRepositoryImpl(this, ApiService.create(applicationContext))
        databaseRepository = DatabaseRepositoryImpl(AmlabDatabase.getDatabase(this).dao(), applicationContext)
        viewModelFactory = ViewModelFactory(
                GetChannelPlaylistsUseCase(apiRepository, databaseRepository),
                GetNetworkStatusUseCase(apiRepository),
                GetPlaylistVideosUseCase(apiRepository),
                GetVideoDetailsUseCase(apiRepository),
                GetAllVideosListUseCase(apiRepository),
                GetAboutChannelUseCase(apiRepository),
                GetMessagesUseCase(databaseRepository),
                DeleteMessagesUseCase(databaseRepository)
        )

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(NotificationOpenedHandler(this))
                .setNotificationReceivedHandler(NotificationReceivedHandler(SaveNotificationUseCase(databaseRepository)))
                .init()
//        OneSignal.sendTag("amlab", "true")
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
    }
}
package ru.goodibunakov.amlabvideo

import android.app.Application
import com.facebook.stetho.Stetho
import com.onesignal.OneSignal
import ru.goodibunakov.amlabvideo.api.ApiService
import ru.goodibunakov.amlabvideo.data.database.AmlabDatabase
import ru.goodibunakov.amlabvideo.data.notifications.NotificationOpenedHandler
import ru.goodibunakov.amlabvideo.data.repositories.ApiRepositoryImpl
import ru.goodibunakov.amlabvideo.data.repositories.DatabaseRepositoryImpl
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import ru.goodibunakov.amlabvideo.domain.usecase.DeleteMessagesUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.DeleteStarFromDbUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetAboutChannelUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetAllVideosListUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetMessagesUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetNetworkStatusUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetPlaylistVideosUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetStarsFromDbUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetVideoDetailsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.SaveNotificationUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.SaveStarToDbUseCase
import ru.goodibunakov.amlabvideo.presentation.viewmodels.ViewModelFactory

class AmlabApplication : Application() {

    companion object {
        lateinit var apiRepository: ApiRepository
        lateinit var databaseRepository: DatabaseRepository
        lateinit var viewModelFactory: ViewModelFactory
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Stetho.initializeWithDefaults(this)

        apiRepository = ApiRepositoryImpl(this, ApiService.create(applicationContext))
        databaseRepository = DatabaseRepositoryImpl(AmlabDatabase.getDatabase(this).dao())
        viewModelFactory = ViewModelFactory(
            GetChannelPlaylistsUseCase(apiRepository, databaseRepository),
            GetNetworkStatusUseCase(apiRepository),
            GetPlaylistVideosUseCase(apiRepository, databaseRepository),
            GetVideoDetailsUseCase(apiRepository),
            GetAllVideosListUseCase(apiRepository),
            GetAboutChannelUseCase(apiRepository),
            GetMessagesUseCase(databaseRepository),
            DeleteMessagesUseCase(databaseRepository),
            SaveStarToDbUseCase(apiRepository, databaseRepository),
            DeleteStarFromDbUseCase(databaseRepository),
            GetStarsFromDbUseCase(databaseRepository)
        )

        OneSignal.initWithContext(this)
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
        OneSignal.setNotificationOpenedHandler(NotificationOpenedHandler(this))
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.setAppId(BuildConfig.ONE_SIGNAL_APP_ID)
    }

    fun getSaveNotificationUseCase() = SaveNotificationUseCase(databaseRepository)

}
package ru.goodibunakov.amlabvideo

import android.app.Application
import com.facebook.stetho.Stetho
import com.onesignal.OneSignal
import ru.goodibunakov.amlabvideo.api.ApiService
import ru.goodibunakov.amlabvideo.data.notifications.NotificationOpenedHandler
import ru.goodibunakov.amlabvideo.data.database.AmlabDatabase
import ru.goodibunakov.amlabvideo.data.repositories.ApiRepositoryImpl
import ru.goodibunakov.amlabvideo.data.repositories.DatabaseRepositoryImpl
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import ru.goodibunakov.amlabvideo.domain.usecase.*
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
        databaseRepository = DatabaseRepositoryImpl(AmlabDatabase.getDatabase(this).dao(), applicationContext)
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
        OneSignal.setAppId("472232b8-fd21-4270-bf9c-77df1c14da6f")
    }

    fun getSaveNotificationUseCase() = SaveNotificationUseCase(databaseRepository)

}
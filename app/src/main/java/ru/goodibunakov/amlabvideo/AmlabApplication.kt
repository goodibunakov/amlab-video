package ru.goodibunakov.amlabvideo

import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.onesignal.OneSignal
import ru.goodibunakov.amlabvideo.api.ApiService
import ru.goodibunakov.amlabvideo.data.database.PlaylistsDatabase
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

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init()

        apiRepository = ApiRepositoryImpl(this, ApiService.create())
        databaseRepository = DatabaseRepositoryImpl(PlaylistsDatabase.getDatabase(this).playlistsDao())
        viewModelFactory = ViewModelFactory(
                GetChannelPlaylistsUseCase(apiRepository, databaseRepository),
                GetNetworkStatusUseCase(apiRepository),
                GetPlaylistVideosUseCase(apiRepository),
                GetVideoDetailsUseCase(apiRepository),
                GetAllVideosListUseCase(apiRepository),
                getString(R.string.new_videos)
        )
    }
}
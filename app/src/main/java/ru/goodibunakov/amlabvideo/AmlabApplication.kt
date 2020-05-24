package ru.goodibunakov.amlabvideo

import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import ru.goodibunakov.amlabvideo.api.ApiService
import ru.goodibunakov.amlabvideo.data.repositories.ApiRepositoryImpl
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.usecase.*
import ru.goodibunakov.amlabvideo.presentation.viewmodels.ViewModelFactory

class AmlabApplication : MultiDexApplication() {

    companion object {
        //        lateinit var fileRepository: FileRepository
        lateinit var apiRepository: ApiRepository
        lateinit var viewModelFactory: ViewModelFactory
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Stetho.initializeWithDefaults(this)

        apiRepository = ApiRepositoryImpl(this, ApiService.create())
        viewModelFactory = ViewModelFactory(
                GetChannelPlaylistsUseCase(apiRepository),
                GetNetworkStatusUseCase(apiRepository),
                GetPlaylistVideosUseCase(apiRepository),
                GetVideoDetailsUseCase(apiRepository),
                GetAllVideosListUseCase(apiRepository),
                getString(R.string.new_videos)
        )
    }
}
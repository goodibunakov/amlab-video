package ru.goodibunakov.amlabvideo

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.api.ApiService
import ru.goodibunakov.amlabvideo.data.repositories.ApiRepositoryImpl
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.usecase.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetNetworkStatusUseCase
import ru.goodibunakov.amlabvideo.domain.usecase.GetPlaylistVideosUseCase
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
                GetPlaylistVideosUseCase(apiRepository)
        )
        //это для первого экрана == все видео
        apiRepository.getAllVideosList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    it.items
                }
                .subscribe({
                    Log.d("debug", "${it}")
                }, {
                    Log.d("debug", "${it.localizedMessage}")
                })

    }
}
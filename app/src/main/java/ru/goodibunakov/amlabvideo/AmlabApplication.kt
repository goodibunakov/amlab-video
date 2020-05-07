package ru.goodibunakov.amlabvideo

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.amlabvideo.api.ApiService
import ru.goodibunakov.amlabvideo.data.mappers.ToPlaylistsEntityMapper
import ru.goodibunakov.amlabvideo.data.repositories.ApiRepositoryImpl
import ru.goodibunakov.amlabvideo.domain.ApiRepository

class AmlabApplication : MultiDexApplication() {

    companion object {
        //        lateinit var fileRepository: FileRepository
        lateinit var apiRepository: ApiRepository
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Stetho.initializeWithDefaults(this)

        apiRepository = ApiRepositoryImpl(this, ApiService.create())
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

//        apiRepository.getPlayLists()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map {
//                    ToPlaylistsEntityMapper().map(it)
//                }
//                .subscribe({
//                    Log.d("debug", "$it \n ${it.size}")
//                }, {
//                    Log.d("debug", "${it.localizedMessage}")
//                })

    }
}
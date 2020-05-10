package ru.goodibunakov.amlabvideo.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.goodibunakov.amlabvideo.api.ApiService
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.videos.AllVideosDTO
import ru.goodibunakov.amlabvideo.domain.ApiRepository


class ApiRepositoryImpl(
        context: Context,
        private val apiService: ApiService
) : ApiRepository {

    /**
     * https://blog.mindorks.com/implement-caching-in-android-using-rxjava-operators
     */

    private var playlistsList: PlaylistsDTO? = null
    var networkConnected = BehaviorSubject.create<ConnectedStatus>()

    companion object {
        const val SKIP = 0
        const val LIMIT = Integer.MAX_VALUE
    }

    init {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo?.isConnected ?: false
        networkConnected.onNext(if (isConnected) ConnectedStatus.YES else ConnectedStatus.NO)

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                networkConnected.onNext(ConnectedStatus.NO)
            }

            override fun onAvailable(network: Network) {
                networkConnected.onNext(ConnectedStatus.YES)
            }
        }

        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun networkConnected(): BehaviorSubject<ConnectedStatus> = networkConnected

    override fun getAllVideosList(): Observable<AllVideosDTO> {
        return apiService.getAllVideos()
    }

    override fun getPlayLists(): Observable<PlaylistsDTO> {
//        return Observable.create { emitter: ObservableEmitter<PlaylistsDTO> ->
//            playlistsList?.let {
//                emitter.onNext(playlistsList!!)
//            }
//            emitter.onComplete()
//        }
//
//        return Observable.just(playlistsList)


        return apiService.getPlaylists()
                .doOnNext { playlistsList = it.copy() }
    }


//    override fun loadSettlementByCoordinates(
//        latitude: Double,
//        longitude: Double
//    ): Observable<SettlementDTO> {
//        return apiService.getSettlementByCoordinates(latitude, longitude)
//    }

//    override fun loadSettlementList(): Observable<List<ListItem>> {
//        return apiService.getSettlementList()
//            .flatMapIterable { it.all }
//            .map { ToSettlementEntityImplMapper().map(it) }
//            .toList()
//            .toObservable()
//            .map { sortList(it) }
//    }

//    override fun getSettlementsByQuery(query: String): Observable<List<SettlementDTO>> {
//        return apiService.getSettlementsByQuery(query, SKIP, LIMIT)
//            .map { it.data }
//    }
}
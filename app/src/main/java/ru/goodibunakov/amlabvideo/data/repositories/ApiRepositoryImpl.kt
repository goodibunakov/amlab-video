package ru.goodibunakov.amlabvideo.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.goodibunakov.amlabvideo.api.ApiService
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.video.VideoDTO
import ru.goodibunakov.amlabvideo.api.dto.video_details.VideoDetailsDTO
import ru.goodibunakov.amlabvideo.api.dto.videos_all.AllVideosDTO
import ru.goodibunakov.amlabvideo.domain.ApiRepository


class ApiRepositoryImpl(
        context: Context,
        private val apiService: ApiService
) : ApiRepository {

    /**
     * https://blog.mindorks.com/implement-caching-in-android-using-rxjava-operators
     */

    private var playlistsList: PlaylistsDTO? = null
    var networkConnected = BehaviorSubject.createDefault(ConnectedStatus.UNKNOWN)

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
        return networkConnected
                .filter { it == ConnectedStatus.YES }
                .flatMap { apiService.getAllVideos() }
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


        return networkConnected
                .filter { it == ConnectedStatus.YES }
                .flatMap { apiService.getPlaylists() }
                .doOnNext { playlistsList = it.copy() }
    }

    override fun getPlaylistVideos(playlistId: String): Observable<VideoDTO> {
        return networkConnected
                .filter { it == ConnectedStatus.YES }
                .flatMap { apiService.getPlaylistVideos(playlistId = playlistId) }
    }

    override fun getVideoDetails(id: String): Observable<VideoDetailsDTO> {
        return networkConnected
                .filter { it == ConnectedStatus.YES }
                .flatMap { apiService.getVideoDetails(id = id) }
    }
}
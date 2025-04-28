package ru.goodibunakov.amlabvideo.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.goodibunakov.amlabvideo.api.ApiService
import ru.goodibunakov.amlabvideo.api.dto.channel_details.BrandingDTO
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.video.VideoDTO
import ru.goodibunakov.amlabvideo.api.dto.video_details.VideoDetailsDTO
import ru.goodibunakov.amlabvideo.api.dto.videos_all.AllVideosDTO
import ru.goodibunakov.amlabvideo.data.mappers.ToVideoItemModelMapper
import ru.goodibunakov.amlabvideo.data.model.VideoItemModel
import ru.goodibunakov.amlabvideo.domain.ApiRepository


class ApiRepositoryImpl(
        context: Context,
        private val apiService: ApiService
) : ApiRepository {

    /**
     * https://blog.mindorks.com/implement-caching-in-android-using-rxjava-operators
     */

    private var playlistsList: PlaylistsDTO? = null
    private var playlistItems: MutableList<VideoItemModel> = mutableListOf()
    var networkConnected = BehaviorSubject.createDefault(ConnectedStatus.UNKNOWN)

    init {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo?.isConnected == true
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

    override fun getMoreAllVideosList(pageToken: String): Observable<AllVideosDTO> {
        return networkConnected
                .filter { it == ConnectedStatus.YES }
                .flatMap { apiService.getAllVideos(pageToken = pageToken) }
    }

    override fun getChannelDetails(): Observable<BrandingDTO> {
        return networkConnected
                .filter { it == ConnectedStatus.YES }
                .flatMap { apiService.getChannelDetails() }
    }

    override fun getPlayLists(): Observable<PlaylistsDTO> {
        return networkConnected
                .filter { it == ConnectedStatus.YES }
                .flatMap { apiService.getPlaylists() }
                .doOnNext { playlistsList = it.copy() }
    }

    override fun getPlaylistVideos(playlistId: String, pageToken: String?): Observable<VideoDTO> {
        return networkConnected
                .filter { it == ConnectedStatus.YES }
                .flatMap {
                    apiService.getPlaylistVideos(playlistId = playlistId, pageToken = pageToken
                            ?: "")
                }
                .doOnNext { playlistItems.addAll(ToVideoItemModelMapper.map(it)) }
    }

    override fun getVideoDetails(id: String): Observable<VideoDetailsDTO> {
        return networkConnected
                .filter { it == ConnectedStatus.YES }
                .flatMap { apiService.getVideoDetails(id = id) }
    }

    override fun clearPlaylistItems() {
        playlistItems.clear()
    }

    override fun getItemById(videoId: String): VideoItemModel? {
        return playlistItems.firstOrNull { it.videoId == videoId }
                ?.copy(star = true)
    }
}
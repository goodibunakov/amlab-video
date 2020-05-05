package ru.goodibunakov.amlabvideo.data.repositories

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.api.ApiService
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.videos.AllVideosDTO
import ru.goodibunakov.amlabvideo.domain.ApiRepository

class ApiRepositoryImpl(private val apiService: ApiService) : ApiRepository {

    private lateinit var playlistsList: PlaylistsDTO

    companion object {
        const val SKIP = 0
        const val LIMIT = Integer.MAX_VALUE
    }

    override fun getAllVideosList(): Observable<AllVideosDTO> {
        return apiService.getLastVideos()
    }

    override fun getPlayLists(): Observable<PlaylistsDTO> {
        return if (::playlistsList.isInitialized) {
            Observable.just(playlistsList)
        } else {
            apiService.getPlaylists()
                    .doOnNext { playlistsList = it }
        }
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
package ru.goodibunakov.amlabvideo.domain.usecase

import android.util.Log
import io.reactivex.Maybe
import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.mappers.ToPlaylistsEntityMapper
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import ru.goodibunakov.amlabvideo.domain.UseCase
import ru.goodibunakov.amlabvideo.domain.entity.PlaylistsEntity

class GetChannelPlaylistsUseCase(
        private val apiRepository: ApiRepository,
        private val databaseRepository: DatabaseRepository
) : UseCase<Unit, List<PlaylistsEntity>>() {

    override fun buildObservable(): Observable<out List<PlaylistsEntity>> {
        return Observable.concat(
                databaseRepository.getPlaylists().toObservable(),
                apiRepository.getPlayLists()
                        .doOnNext {
                            databaseRepository.insertPlaylists(it)
                                    .subscribe({
                                        Log.d("debug", "GetChannelPlaylistsUseCase insert completed")
                                    }, {
                                        Log.d("debug", "GetChannelPlaylistsUseCase insert error = $it")
                                    })
                        }
        )
                .firstOrError()
                .doOnError { Log.d("debug", "jopa $it") }
                .map { ToPlaylistsEntityMapper.map(it) }
                .toObservable()
    }

    fun updatePlaylistsToDatabase(): Maybe<List<PlaylistsEntity>> {
        return apiRepository.getPlayLists()
                .firstElement()
                .doOnSuccess {
                    databaseRepository.updatePlaylists(it)
                }
                .map { ToPlaylistsEntityMapper.map(it) }
    }
}
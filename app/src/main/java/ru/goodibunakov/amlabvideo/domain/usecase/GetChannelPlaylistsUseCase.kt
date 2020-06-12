package ru.goodibunakov.amlabvideo.domain.usecase

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.data.mappers.ToPlaylistsEntityMapper
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.DatabaseRepository
import ru.goodibunakov.amlabvideo.domain.UseCase
import ru.goodibunakov.amlabvideo.domain.entity.PlaylistsEntity

class GetChannelPlaylistsUseCase(
        private val apiRepository: ApiRepository,
        private val databaseRepository: DatabaseRepository
) : UseCase<Unit, List<PlaylistsEntity>>() {

    private lateinit var playlistsCache: Observable<PlaylistsDTO>

    //    TODO получить список листов из кэша, если нет то из БД, если нет то из сети и закэшировать
    override fun buildObservable(): Observable<out List<PlaylistsEntity>> {
        return Observable.concat(
                databaseRepository.getPlaylists().toObservable(),
                apiRepository.getPlayLists()
                        .doOnNext {
                            databaseRepository.insertPlaylists(it)
                                    .subscribe({
                                        Log.d("debug", "insert completed")
                                    }, {
                                        Log.d("debug", "insert error = $it")
                                    })
                        }
        )
                .firstOrError()
                .doOnError { Log.d("debug", "jopa $it") }
                .map { ToPlaylistsEntityMapper.map(it) }
                .toObservable()
    }

    fun updatePlaylistsToDatabase(): Completable {
        return apiRepository.getPlayLists()
                .flatMapCompletable { databaseRepository.insertPlaylists(it) }
    }
}
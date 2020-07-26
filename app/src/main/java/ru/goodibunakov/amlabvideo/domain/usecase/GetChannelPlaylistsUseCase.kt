package ru.goodibunakov.amlabvideo.domain.usecase

import android.util.Log
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
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
                                    }, { throwable ->
                                        Log.d("debug", "GetChannelPlaylistsUseCase insert error = $throwable")
                                    })
                        }
        )
                .firstOrError()
                .doOnError { Log.d("debug", "GetChannelPlaylistsUseCase error $it") }
                .map { ToPlaylistsEntityMapper.map(it) }
                .flatMap { Single.fromCallable { setNewVideosPlaylistToFirstPlace(it) } }
                .toObservable()
    }

    fun updatePlaylistsToDatabase(): Maybe<List<PlaylistsEntity>> {
        return apiRepository.getPlayLists()
                .firstElement()
                .doOnSuccess {
                    databaseRepository.updatePlaylists(it)
                }
                .map { ToPlaylistsEntityMapper.map(it) }
                .flatMap { Maybe.fromCallable { setNewVideosPlaylistToFirstPlace(it) } }
    }


    private fun setNewVideosPlaylistToFirstPlace(list: List<PlaylistsEntity>): List<PlaylistsEntity> {
        val newVideosPlaylistIndex = list.indexOfFirst { it.listId == PLAYLIST_NEW_VIDEO_ID }
        return if (newVideosPlaylistIndex == 0 || newVideosPlaylistIndex == -1) {
            list
        } else {
            val listForEdit = list.toMutableList()
            val newVideosPlaylist = listForEdit.removeAt(newVideosPlaylistIndex)
            listForEdit.apply { add(0, newVideosPlaylist) }
        }
    }

    companion object {
        private const val PLAYLIST_NEW_VIDEO_ID = "PLvnAyibYVyhvCG8eKGsl57ClQ6e21aofa"
    }
}
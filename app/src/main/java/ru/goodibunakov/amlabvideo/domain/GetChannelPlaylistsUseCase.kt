package ru.goodibunakov.amlabvideo.domain

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.mappers.ToPlaylistsEntityMapper
import ru.goodibunakov.amlabvideo.domain.entity.PlaylistsEntity

class GetChannelPlaylistsUseCase(private val apiRepository: ApiRepository) : UseCase<List<PlaylistsEntity>, List<PlaylistsEntity>> {

    //    TODO получить список листов и отсортировать как-то - по алфавиту или по дате?
    override fun buildObservable(): Observable<out List<PlaylistsEntity>> {
        return apiRepository.getPlayLists()
                .map { ToPlaylistsEntityMapper.map(it) }
    }
}
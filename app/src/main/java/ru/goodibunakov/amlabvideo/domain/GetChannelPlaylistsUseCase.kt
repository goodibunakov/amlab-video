package ru.goodibunakov.amlabvideo.domain

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.domain.entity.PlaylistsEntity

class GetChannelPlaylistsUseCase : UseCase<List<PlaylistsEntity>, List<PlaylistsEntity>> {

    override fun buildObservable(): Observable<out List<PlaylistsEntity>> {
        TODO("Not yet implemented")
    }

//    получить список листов и отсортировать как-то - по алфавиту или по дате?

}
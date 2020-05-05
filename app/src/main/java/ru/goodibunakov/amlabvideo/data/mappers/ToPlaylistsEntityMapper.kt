package ru.goodibunakov.amlabvideo.data.mappers

import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.domain.entity.PlaylistsEntity

class ToPlaylistsEntityMapper : Mapper<PlaylistsDTO, List<PlaylistsEntity>> {

    override fun map(from: PlaylistsDTO): List<PlaylistsEntity> {
        return from.items.map {
            PlaylistsEntity(
                    it.id,
                    it.snippet.title,
                    it.snippet.thumbnails.standard.url)
        }
    }
}
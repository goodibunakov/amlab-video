package ru.goodibunakov.amlabvideo.presentation.mappers

import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.domain.entity.PlaylistsEntity
import ru.goodibunakov.amlabvideo.presentation.model.PlaylistsModelUI

object ToPlaylistsModelUIMapper : Mapper<List<PlaylistsEntity>, List<PlaylistsModelUI>> {

    override fun map(from: List<PlaylistsEntity>): List<PlaylistsModelUI> {
        return from.map {
            PlaylistsModelUI(
                    it.title,
                    it.listId
            )
        }
    }
}
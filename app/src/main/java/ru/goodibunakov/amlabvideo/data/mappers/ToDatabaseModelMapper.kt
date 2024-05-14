package ru.goodibunakov.amlabvideo.data.mappers

import com.google.gson.Gson
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.data.model.PlaylistsDTODatabase
import ru.goodibunakov.amlabvideo.domain.Mapper

object ToDatabaseModelMapper : Mapper<PlaylistsDTO, PlaylistsDTODatabase> {

    override fun map(from: PlaylistsDTO): PlaylistsDTODatabase {
        return PlaylistsDTODatabase(playlistsDTOjson = Gson().toJson(from))
    }
}
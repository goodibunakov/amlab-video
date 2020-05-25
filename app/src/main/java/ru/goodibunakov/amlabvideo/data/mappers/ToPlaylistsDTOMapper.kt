package ru.goodibunakov.amlabvideo.data.mappers

import com.google.gson.Gson
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTODatabase
import ru.goodibunakov.amlabvideo.domain.Mapper


object ToPlaylistsDTOMapper : Mapper<PlaylistsDTODatabase, PlaylistsDTO> {

    override fun map(from: PlaylistsDTODatabase): PlaylistsDTO {
        return Gson().fromJson(from.playlistsDTOjson, PlaylistsDTO::class.java)
    }
}
package ru.goodibunakov.amlabvideo.domain.entity

data class VideoEntity(
    val title: String,
    val videoId: String,
    val playlistId: String,
    val imageUrl: String?,
    val createdDate: String,
    val star: Boolean
) {
    override fun equals(other: Any?): Boolean {
        other as VideoEntity
        return this.videoId == other.videoId
    }

    override fun hashCode(): Int {
        return videoId.hashCode()
    }
}
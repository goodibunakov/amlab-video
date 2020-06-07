package ru.goodibunakov.amlabvideo.presentation.mappers

import ru.goodibunakov.amlabvideo.domain.Mapper
import ru.goodibunakov.amlabvideo.domain.entity.VideoEntity
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object ToVideoModelUIMapper : Mapper<List<VideoEntity>, List<VideoUIModel>> {

    override fun map(from: List<VideoEntity>): List<VideoUIModel> {
        return from.map {
            VideoUIModel(
                    title = it.title,
                    videoId = it.videoId,
                    imageUrl = it.imageUrl,
                    createdDate = convertDate(it.createdDate)
            )
        }
    }

    private fun convertDate(input: String?): String {
        input ?: return ""

        var formattedDate: String? = null
        try {
            val convertedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(input)
            convertedDate?.let {
                formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(it)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
        return formattedDate ?: ""
    }
}
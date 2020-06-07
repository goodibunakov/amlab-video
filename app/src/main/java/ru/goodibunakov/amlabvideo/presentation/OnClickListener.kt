package ru.goodibunakov.amlabvideo.presentation

import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel

interface OnClickListener  {
    fun onItemClick(videoItem: VideoUIModel)
}
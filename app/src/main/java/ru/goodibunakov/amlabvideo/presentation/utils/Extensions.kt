package ru.goodibunakov.amlabvideo.presentation.utils

import android.view.View
import android.view.animation.BounceInterpolator
import androidx.core.view.ViewCompat
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.subjects.CompletableSubject


fun MutableLiveData<String>.setValidatedValue(newValue: String) {
    if (value != newValue) {
        value = newValue
    }
}

fun View.zoomIn(duration: Long = 1000L): Completable {
    val animationSubject = CompletableSubject.create()
    return animationSubject.doOnSubscribe {
        ViewCompat.animate(this)
                .setDuration(duration)
                .scaleXBy(.2f)
                .scaleYBy(.2f)
                .setInterpolator(BounceInterpolator())
                .withEndAction {
                    animationSubject.onComplete()
                }
    }
}

fun <T> MutableLiveData<List<T>>.addAll(list: List<T>) {
    val previousList = value
    value = previousList?.toMutableList()?.apply { addAll(list) }
}
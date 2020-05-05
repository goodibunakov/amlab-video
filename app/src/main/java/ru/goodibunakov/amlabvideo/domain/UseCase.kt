package ru.goodibunakov.amlabvideo.domain

import io.reactivex.Observable

interface UseCase<in Data, out Response> {
    /**
     * Creates observable for this use case (here is all of business logic)
     */
    fun buildObservable(): Observable<out Response>

}
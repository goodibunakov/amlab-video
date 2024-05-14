package ru.goodibunakov.amlabvideo.domain

import io.reactivex.Observable

abstract class UseCase<in Data, out Response> {
    /**
     * Creates observable for this use case (here is all of business logic)
     */
    abstract fun buildObservable(): Observable<out Response>

    /**
     * Sets external data for correct case execution
     */
    open fun set(data: Data) {}
}
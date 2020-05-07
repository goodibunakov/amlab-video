package ru.goodibunakov.amlabvideo.domain

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus

class GetNetworkStatusUseCase(private val apiRepository: ApiRepository): UseCase<Unit, ConnectedStatus> {

    override fun buildObservable(): Observable<ConnectedStatus> {
        return apiRepository.networkConnected()
    }
}
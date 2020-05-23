package ru.goodibunakov.amlabvideo.domain.usecase

import io.reactivex.Observable
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
import ru.goodibunakov.amlabvideo.domain.ApiRepository
import ru.goodibunakov.amlabvideo.domain.UseCase

class GetNetworkStatusUseCase(private val apiRepository: ApiRepository): UseCase<Unit, ConnectedStatus>() {

    override fun buildObservable(): Observable<ConnectedStatus> {
        return apiRepository.networkConnected()
    }
}
package ru.goodibunakov.amlabvideo.data.repositories

import io.reactivex.Completable
import io.reactivex.Single
import java.io.File


class FileRepositoryImpl(filesPath: String)
//    : FileRepository
{

//    private val filename = "$filesPath/settlement.txt"
//
//    override fun saveSettlement(item: SettlementEntityImpl): Completable {
//        return Completable.fromAction {
//            File(filename).writeText(
//                ToStorageSettlementMapper().map(item)
//            )
//        }
//    }
//
//    override fun getSettlement(): Single<SettlementEntityImpl> {
//        return Single.fromCallable { File(filename).readText() }
//            .map { ToSettlementEntityMapper().map(it) as SettlementEntityImpl }
//    }
}
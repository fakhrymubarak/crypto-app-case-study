package com.hightech.cryptofeed.cache

import com.hightech.cryptofeed.domain.CoinInfo
import com.hightech.cryptofeed.domain.CryptoFeed
import com.hightech.cryptofeed.domain.LoadCryptoFeedResult
import com.hightech.cryptofeed.domain.Raw
import com.hightech.cryptofeed.domain.Usd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

typealias SaveResult = Exception?
class NotFound : Exception()
class DatabaseError : Exception()

class CacheCryptoFeedUseCase(
    private val store: RoomCryptoFeedStore,
    private val currentDate: Date
) {

    fun load(): Flow<LoadCryptoFeedResult> = flow {
        store.load().collect { result ->
            when (result) {
                is RoomResult.Failure -> emit(LoadCryptoFeedResult.Failure(DatabaseError()))
                is RoomResult.Success -> {
                    if (result.data.isEmpty()) {
                        emit(LoadCryptoFeedResult.Failure(NotFound()))
                    } else if (currentDate.time - result.cachedDate.time <= 8640000) {
                        val domainResult = result.data.toModels()
                        emit(LoadCryptoFeedResult.Success(domainResult))
                    } else store.deleteCache().collect {
                        emit(LoadCryptoFeedResult.Failure(NotFound()))
                    }
                }
            }
        }
    }

    fun save(feeds: List<CryptoFeed>): Flow<SaveResult> = flow {
        store.deleteCache().collect { deleteError ->
            if (deleteError != null) {
                emit(deleteError)
            } else {
                store.insert(feeds.toLocal(), currentDate).collect { insertError ->
                    emit(insertError)
                }
            }
        }
    }

    private fun List<LocalCryptoFeed>.toModels(): List<CryptoFeed> {
        return map { localCryptoFeed ->
            CryptoFeed(
                CoinInfo(
                    localCryptoFeed.coinInfo.id,
                    localCryptoFeed.coinInfo.name,
                    localCryptoFeed.coinInfo.fullName,
                    localCryptoFeed.coinInfo.imageUrl
                ),
                Raw(Usd(localCryptoFeed.raw.usd.price, localCryptoFeed.raw.usd.changePctDay)),
            )
        }
    }

    private fun List<CryptoFeed>.toLocal(): List<LocalCryptoFeed> {
        return map {
            LocalCryptoFeed(
                coinInfo = LocalCoinInfo(
                    id = it.coinInfo.id,
                    name = it.coinInfo.name,
                    fullName = it.coinInfo.fullName,
                    imageUrl = it.coinInfo.imageUrl
                ),
                raw = LocalRaw(
                    usd = LocalUsd(
                        price = it.raw.usd.price,
                        changePctDay = it.raw.usd.changePctDay
                    )
                )
            )
        }
    }
}

data class LocalCryptoFeed(
    val coinInfo: LocalCoinInfo,
    val raw: LocalRaw,
)

data class LocalCoinInfo(
    val id: String,
    val name: String,
    val fullName: String,
    val imageUrl: String
)

data class LocalRaw(
    val usd: LocalUsd
)

data class LocalUsd(
    val price: Double,
    val changePctDay: Float
)
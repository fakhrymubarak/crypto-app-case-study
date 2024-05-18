package com.hightech.cryptofeed.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

sealed class RoomResult {
    data class Success(val data: List<LocalCryptoFeed>, val cachedDate: Date) : RoomResult()
    data class Failure(val exception: Exception) : RoomResult()
}

class RoomCryptoFeedStore {
    fun deleteCache(): Flow<Exception?> = flow {}

    fun insert(feeds: List<LocalCryptoFeed>, timestamp: Date): Flow<Exception?> = flow {}

    fun load(): Flow<RoomResult> = flow {}
}
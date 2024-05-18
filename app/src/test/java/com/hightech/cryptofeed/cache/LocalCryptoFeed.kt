package com.hightech.cryptofeed.cache

val cryptoFeedLocal = listOf(
    LocalCryptoFeed(
        LocalCoinInfo(
            "1",
            "BTC",
            "Bitcoin",
            "imageUrl",
        ),
        LocalRaw(
            LocalUsd(
                1.0,
                1F,
            ),
        )
    ),
    LocalCryptoFeed(
        LocalCoinInfo(
            "2",
            "BTC 2",
            "Bitcoin 2",
            "imageUrl"
        ),
        LocalRaw(
            LocalUsd(
                2.0,
                2F,
            ),
        )
    ),
)
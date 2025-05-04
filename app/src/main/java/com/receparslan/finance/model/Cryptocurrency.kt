package com.receparslan.finance.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class CryptocurrencyList(
    @SerializedName("coins")
    val cryptocurrencies: List<Cryptocurrency>
)

@Entity(tableName = "cryptocurrency")
data class Cryptocurrency(
    @PrimaryKey
    var id: String,

    var name: String,
    var symbol: String,
    var image: String,

    @SerializedName("current_price")
    var currentPrice: Double,

    @SerializedName("price_change_percentage_24h")
    var priceChangePercentage24h: Double,

    @SerializedName("last_updated")
    var lastUpdated: String
)

data class KlineData(
    val openTime: Long,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val closeTime: Long
)
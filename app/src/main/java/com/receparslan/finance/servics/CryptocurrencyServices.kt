package com.receparslan.finance.servics

import com.receparslan.finance.model.Cryptocurrency
import com.receparslan.finance.model.CryptocurrencyList
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// This constant defines the base URL for the CoinGecko API
private const val BASE_URL = "https://api.coingecko.com/api/v3/"

// This constant defines the base URL for the Binance API
private const val BASE_URL_2 = "https://api.binance.com/api/v3/"

// This variable creates a Retrofit instance for making API calls to the CoinGecko API
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

// This variable creates a Retrofit instance for making API calls to the Binance API
private val retrofit2 = Retrofit.Builder()
    .baseUrl(BASE_URL_2)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

// This interface defines the API endpoints for fetching cryptocurrency data
interface CryptocurrencyApiService {
    @GET("coins/markets?vs_currency=usd&per_page=250")
    suspend fun getCryptocurrencyListByPage(@Query("page") page: Int): Response<List<Cryptocurrency>>

    @GET("coins/markets?vs_currency=usd")
    suspend fun getCryptocurrencyListByNames(@Query("names") names: String): Response<List<Cryptocurrency>>

    @GET("coins/markets?vs_currency=usd")
    suspend fun getCryptocurrencyByIds(@Query("ids") ids: String): Response<List<Cryptocurrency>>

    @GET("search")
    suspend fun searchCryptocurrency(@Query("query") query: String): Response<CryptocurrencyList>
}

// This interface defines the API endpoints for fetching historical data from Binance
interface BinanceApiService {
    @GET("klines?limit=1000")
    suspend fun getHistoricalDataByRange(
        @Query("symbol") symbol: String,
        @Query("startTime") startTime: Long,
        @Query("endTime") endTime: Long,
        @Query("interval") interval: String
    ): Response<List<*>>
}

// This object provides a singleton instance of the CryptocurrencyApiService and BinanceApiService
object CryptocurrencyAPI {
    @Suppress("unused", "RedundantSuppression")
    val retrofitService: CryptocurrencyApiService by lazy {
        retrofit.create(CryptocurrencyApiService::class.java)
    }

    @Suppress("unused", "RedundantSuppression")
    val binanceService: BinanceApiService by lazy {
        retrofit2.create(BinanceApiService::class.java)
    }
}
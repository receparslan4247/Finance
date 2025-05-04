package com.receparslan.finance.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.receparslan.finance.database.CryptocurrencyDatabase
import com.receparslan.finance.model.Cryptocurrency
import com.receparslan.finance.model.KlineData
import com.receparslan.finance.servics.CryptocurrencyAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

class CryptocurrencyViewModel(application: Application) : AndroidViewModel(application) {
    // State variables to hold the list of cryptocurrencies and their search results
    val cryptocurrencyList = mutableStateListOf<Cryptocurrency>()
    val cryptocurrencySearchList = mutableStateListOf<Cryptocurrency>()
    val cryptocurrencyGainerList = mutableStateListOf<Cryptocurrency>()
    val cryptocurrencyLoserList = mutableStateListOf<Cryptocurrency>()
    val savedCryptocurrencyList = mutableStateListOf<Cryptocurrency>()

    val klineDataHistoryList = mutableStateOf<List<KlineData>>(emptyList()) // List to hold the historical Kline data

    var isLoading = mutableStateOf(false) // Loading state to show/hide loading indicators

    var page = 1

    // This function is called when the ViewModel is initialized to fetch the initial list of cryptocurrencies.
    init {
        addCryptocurrenciesByPage(page) // Fetch the initial list of cryptocurrencies
        setGainersAndLosersList() // Set the gainers and losers list
        setSavedCryptocurrencies() // Set the saved cryptocurrencies
    }

    // This function is used to refresh the home screen by clearing the existing list and fetching new data.
    fun refreshHomeScreen() {
        page = 1
        cryptocurrencyList.clear()
        addCryptocurrenciesByPage(page)
    }

    // This function is used to refresh the gainer and loser screen by clearing the existing lists and fetching new data.
    fun refreshGainerLoserScreen() {
        setGainersAndLosersList()
    }

    // This function is used to refresh the detail screen by clearing the existing Kline data history list and fetching new data.
    fun refreshDetailScreen(symbol: String) {
        refreshHomeScreen()
        refreshGainerLoserScreen()
        klineDataHistoryList.value = emptyList()
        setCryptocurrencyHistory(symbol, System.currentTimeMillis() - 24 * 60 * 60 * 1000L, "1m")
    }

    // This function is used to set the current page number for pagination.
    fun loadMore() {
        if (!isLoading.value) {
            page++
            addCryptocurrenciesByPage(page)
        }
    }

    // This function fetches the list of cryptocurrencies from the API and updates the cryptocurrencyList variable.
    private fun addCryptocurrenciesByPage(page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true // Set loading state to true

            // Make a network request to fetch the list of cryptocurrencies
            repeat(15) { attempt ->
                try {
                    val response = CryptocurrencyAPI.retrofitService.getCryptocurrencyListByPage(page)

                    if (response.isSuccessful) {
                        response.body()?.let {
                            cryptocurrencyList.addAll(it.filter { newItem -> newItem !in cryptocurrencyList })
                        }
                        return@launch
                    }
                } catch (e: Exception) {
                    println("Exception occurred: ${e.message}")
                }

                delay(5000) // Wait for 5 seconds before retrying
            }
        }.invokeOnCompletion { isLoading.value = false } // Set loading state to false when the loading is completed
    }

    // This function fetches a list of cryptocurrencies based on the search query and returns the results.
    fun searchCryptocurrencies(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true // Set loading state to true

            // Make a network request to fetch the list of cryptocurrencies
            repeat(15) { attempt ->
                try {
                    val response = CryptocurrencyAPI.retrofitService.searchCryptocurrency(query)

                    if (response.isSuccessful) {
                        // If the response is successful, add the cryptocurrencies to the search list
                        response.body()?.let { cryptocurrencyList ->
                            val ids = cryptocurrencyList.cryptocurrencies.joinToString(",") { it.id }// Get the list of cryptocurrency IDs

                            try {
                                val response = CryptocurrencyAPI.retrofitService.getCryptocurrencyByIds(ids)

                                // If the response is successful, add the cryptocurrencies to the search list
                                if (response.isSuccessful) {
                                    response.body()?.let {
                                        cryptocurrencySearchList.clear()
                                        cryptocurrencySearchList.addAll(it)
                                    }
                                    return@launch
                                } else {
                                    println("Response unsuccessful: $response")
                                    delay(5000) // Wait for 5 second before retrying
                                }
                            } catch (e: Exception) {
                                println("Exception occurred: ${e.message}")
                            }
                        }
                    } else {
                        println("Response unsuccessful: $response")
                        delay(5000) // Wait for 5 seconds before retrying
                    }
                } catch (e: Exception) {
                    println("Exception occurred: ${e.message}")
                }
            }
        }.invokeOnCompletion { isLoading.value = false } // Set loading state to false when the search is completed
    }

    // This function fetches a list of cryptocurrencies by their IDs and returns the results.
    fun getCryptocurrenciesByIds(ids: List<String>): MutableState<List<Cryptocurrency>> {
        // Create a mutable list to hold the cryptocurrencies
        val cryptocurrenciesHolder = mutableStateListOf<Cryptocurrency>()

        // Create a mutable state to hold the list of cryptocurrencies
        val cryptocurrencies = mutableStateOf<List<Cryptocurrency>>(cryptocurrenciesHolder) // Create a list to hold the cryptocurrencies

        // Check if the list of IDs is empty
        if (ids.isEmpty())
            return cryptocurrencies

        // Make a network request to fetch the list of cryptocurrencies by their IDs
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true // Set loading state to true

            try {
                val response = CryptocurrencyAPI.retrofitService.getCryptocurrencyByIds(ids.joinToString(","))

                if (response.isSuccessful) {
                    // Handle the successful response
                    response.body()?.let {
                        cryptocurrenciesHolder.addAll(it.filter { newItem -> newItem !in cryptocurrencyList })
                    }
                } else {
                    println("Response unsuccessful: $response")
                }
            } catch (e: Exception) {
                println("Exception occurred: ${e.message}")
            }
        }.invokeOnCompletion { isLoading.value = false } // Set loading state to false when the loading is completed

        return cryptocurrencies // Return the list of cryptocurrencies
    }

    // This function fetches the ID of a cryptocurrency by its symbol and returns the ID.
    private suspend fun setCryptocurrencyIdsByNames(names: List<String>) {
        try {
            val query = names.joinToString(",")// Create a query string from the list of names

            val response = withContext(Dispatchers.IO) { CryptocurrencyAPI.retrofitService.getCryptocurrencyListByNames(query) }

            if (response.isSuccessful) {
                // Handle the successful response
                response.body()?.let {
                    it.forEach {
                        for (cryptocurrency in cryptocurrencyGainerList + cryptocurrencyLoserList) {
                            if (cryptocurrency.name == it.name) {
                                cryptocurrency.id = it.id // Set the ID of the cryptocurrency
                                cryptocurrency.lastUpdated =
                                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()) // Set the last updated time of the cryptocurrency
                            }
                        }
                    }
                }
            } else {
                println("Response unsuccessful: $response")
            }
        } catch (e: Exception) {
            println("Exception occurred: ${e.message}")
        }
    }

    // This function fetches the list of gainers and losers from the CoinGecko website using Jsoup.
    private fun setGainersAndLosersList() {
        val url = "https://www.coingecko.com/en/crypto-gainers-losers" // URL for gainers and losers

        val client = OkHttpClient() // Create an instance of OkHttpClient

        // Create a request to fetch the HTML content from the URL
        val request = Request.Builder()
            .url(url)
            .build()

        // Clear the previous lists
        cryptocurrencyGainerList.clear()
        cryptocurrencyLoserList.clear()

        // Make a network request to fetch the HTML content
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true // Set loading state to true

            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    var html: String = response.body?.string() ?: "" // Get the HTML response as a string
                    val document = Jsoup.parse(html) // Parse the HTML response using Jsoup
                    val headers = document.select("tbody") // Select the table body from the HTML document

                    headers.forEachIndexed { index, item ->
                        item.select("tr").forEach {
                            val symbol = it.select("a").select("div > div > div").text() // Get the symbol of the cryptocurrency
                            val name =
                                it.select("a").select("div > div").text().substringBeforeLast("$symbol $symbol")
                                    .trim() // Get the name of the cryptocurrency
                            val image = it.select("img").attr("src").substringBefore("?") // Get the image URL of the cryptocurrency
                            val price = it.select("td")[3].text().substringAfter("$").toDouble() // Get the price of the cryptocurrency
                            val change = it.select("td")[5].text().substringBefore("%").toDouble() // Get the change percentage of the cryptocurrency

                            val cryptocurrency = Cryptocurrency(
                                id = "",
                                name = name,
                                symbol = symbol,
                                image = image,
                                currentPrice = price,
                                priceChangePercentage24h = change,
                                lastUpdated = ""
                            )

                            // Add the cryptocurrency to the appropriate list based on the index
                            if (index == 0) {
                                cryptocurrencyGainerList.add(cryptocurrency)
                            } else {
                                cryptocurrencyLoserList.add(cryptocurrency)
                            }
                        }
                    }

                    // Set the cryptocurrency IDs by names
                    setCryptocurrencyIdsByNames(cryptocurrencyGainerList.map { it.name } + cryptocurrencyLoserList.map { it.name })

                    // Add the gainers and losers to the main cryptocurrency list
                    cryptocurrencyList.addAll(cryptocurrencyGainerList.filter { it !in cryptocurrencyList } + cryptocurrencyLoserList.filter { it !in cryptocurrencyList })
                } else
                    println("Response unsuccessful: $response")
            } catch (e: Exception) {
                println("Exception occurred: ${e.message}")
            }
        }.invokeOnCompletion { isLoading.value = false } // Set loading state to false when the loading is completed
    }

    // This function fetches the historical Kline data for a given cryptocurrency symbol and returns the data as a list.
    fun setCryptocurrencyHistory(symbol: String, startTime: Long, interval: String) {
        val klineDataHistoryListHolder = mutableStateListOf<KlineData>() // Create a mutable list to hold the historical Kline data

        val endTime = (System.currentTimeMillis() / 1000) * 1000 // Get the current time in milliseconds

        // Calculate the interval time in milliseconds based on the selected interval
        val intervalTimeMillis =
            if (interval == "1m") 60 * 1000L else if (interval == "1h") 60 * 60 * 1000L else if (interval == "1d") 24 * 60 * 60 * 1000L else 1

        // Calculate the repeat number based on the interval time
        var repeatNumber = ceil((endTime - startTime) / intervalTimeMillis / 1000F).toInt()

        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true // Set loading state to true

            klineDataHistoryList.value = emptyList() // Clear the existing Kline data history list

            repeat(repeatNumber) { attempt ->
                try {
                    val response = CryptocurrencyAPI.binanceService.getHistoricalDataByRange(
                        symbol = if (symbol.uppercase() == "USDT") "BTCUSDT" else symbol.uppercase() + "USDT",
                        startTime = if (attempt == repeatNumber - 1) startTime - intervalTimeMillis else endTime - (attempt + 1) * intervalTimeMillis * 1000L,
                        endTime = endTime - attempt * intervalTimeMillis * 1000L,
                        interval = interval
                    )
                    if (response.isSuccessful) {
                        // Handle the successful response
                        response.body()?.let {
                            val list = it.map { array ->
                                val array = array as List<*>

                                val data = KlineData(
                                    openTime = (array[0] as Double).toLong(),
                                    open = if (symbol.uppercase() != "USDT") array[1] as String else "1.0",
                                    high = if (symbol.uppercase() != "USDT") array[2] as String else "1.0",
                                    low = if (symbol.uppercase() != "USDT") array[3] as String else "1.0",
                                    close = if (symbol.uppercase() != "USDT") array[4] as String else "1.0",
                                    closeTime = (array[6] as Double).toLong()
                                )

                                data
                            }

                            klineDataHistoryListHolder.addAll(list.filterNot { it in klineDataHistoryListHolder }) // Set the historical Kline data
                        }
                    } else
                        println("Response unsuccessful: $response")
                } catch (e: Exception) {
                    println("Exception occurred: ${e.message}")
                }
            }
        }.invokeOnCompletion {
            isLoading.value = false // Set loading state to false when the loading is completed
            klineDataHistoryList.value = klineDataHistoryListHolder // Update the Kline data history list
        }
    }

    // This function saves a cryptocurrency to the database.
    fun saveCryptocurrency(cryptocurrency: Cryptocurrency) {
        viewModelScope.launch(Dispatchers.IO) {
            val cryptocurrencyDao = CryptocurrencyDatabase.getDatabase(getApplication<Application>().applicationContext).cryptocurrencyDao()

            cryptocurrencyDao.insertCryptocurrency(cryptocurrency)

            // Add the cryptocurrency to the saved list if it is not already present
            withContext(Dispatchers.Main) {
                if (savedCryptocurrencyList.none { it.id == cryptocurrency.id })
                    savedCryptocurrencyList.add(cryptocurrency)
            }
        }
    }

    // This function deletes a cryptocurrency from the database.
    fun deleteCryptocurrency(cryptocurrency: Cryptocurrency) {
        viewModelScope.launch(Dispatchers.IO) {
            val cryptocurrencyDao = CryptocurrencyDatabase.getDatabase(getApplication<Application>().applicationContext).cryptocurrencyDao()

            cryptocurrencyDao.deleteCryptocurrency(cryptocurrency)

            // Remove the cryptocurrency from the saved list if it is present
            if (cryptocurrency.id in savedCryptocurrencyList.map { it.id }) {
                val itemToRemove = savedCryptocurrencyList.filter { it.id == cryptocurrency.id } // Find the cryptocurrency in the saved list

                // Remove the cryptocurrency from the saved list
                savedCryptocurrencyList.removeAll(itemToRemove)
            }
        }
    }

    // This function sets the saved cryptocurrencies by fetching them from the database.
    private fun setSavedCryptocurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            val cryptocurrencyDao = CryptocurrencyDatabase.getDatabase(getApplication<Application>().applicationContext).cryptocurrencyDao()

            savedCryptocurrencyList.addAll(cryptocurrencyDao.getAllCryptocurrencies().filter { it !in savedCryptocurrencyList })
        }
    }
}
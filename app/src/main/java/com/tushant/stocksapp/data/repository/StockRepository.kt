package com.tushant.stocksapp.data.repository

import com.tushant.stocksapp.data.api.StockApi
import com.tushant.stocksapp.data.database.WatchlistDao
import com.tushant.stocksapp.data.database.entities.Watchlist
import com.tushant.stocksapp.data.database.entities.WatchlistStock
import com.tushant.stocksapp.data.models.StockOverview
import com.tushant.stocksapp.data.models.SymbolSearchResponse
import com.tushant.stocksapp.data.models.TopGainersLosersResponse
import com.tushant.stocksapp.utils.Constants.API_KEY
import com.tushant.stocksapp.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val api: StockApi,
    private val watchlistDao: WatchlistDao
) {

    suspend fun getTopGainersLosers(): Flow<NetworkResult<TopGainersLosersResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getTopGainersLosers(apiKey = API_KEY)
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    emit(NetworkResult.Success(data))
                } ?: emit(NetworkResult.Error("Empty response"))
            } else {
                emit(NetworkResult.Error("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    suspend fun getStockOverview(symbol: String): Flow<NetworkResult<StockOverview>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getStockOverview(
                symbol = symbol,
                apiKey = API_KEY
            )
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    if (data.symbol.isNotEmpty()) {
                        emit(NetworkResult.Success(data))
                    } else {
                        emit(NetworkResult.Error("Stock not found"))
                    }
                } ?: emit(NetworkResult.Error("Empty response"))
            } else {
                emit(NetworkResult.Error("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    suspend fun searchSymbol(query: String): Flow<NetworkResult<SymbolSearchResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.searchSymbol(
                keywords = query,
                apiKey = API_KEY
            )
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    emit(NetworkResult.Success(data))
                } ?: emit(NetworkResult.Error("Empty response"))
            } else {
                emit(NetworkResult.Error("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getAllWatchlists(): Flow<List<Watchlist>> = watchlistDao.getAllWatchlists()

    fun getWatchlistStocks(watchlistId: Long): Flow<List<WatchlistStock>> =
        watchlistDao.getWatchlistStocks(watchlistId)

    suspend fun getStockSymbols(watchlistId: Long): List<String> =
        watchlistDao.getStockSymbols(watchlistId)

    suspend fun getStockCount(watchlistId: Long): Int =
        watchlistDao.getStockCount(watchlistId)

    suspend fun isStockInAnyWatchlist(symbol: String): Boolean =
        watchlistDao.isStockInAnyWatchlist(symbol)

    suspend fun createWatchlist(name: String): Long =
        watchlistDao.insertWatchlist(Watchlist(name = name))

    suspend fun addStockToWatchlist(watchlistId: Long, symbol: String) =
        watchlistDao.addStockToWatchlist(watchlistId, symbol)

    suspend fun removeStockFromWatchlist(watchlistId: Long, symbol: String) =
        watchlistDao.removeStockFromWatchlist(watchlistId, symbol)

    suspend fun deleteWatchlist(watchlist: Watchlist) =
        watchlistDao.deleteWatchlist(watchlist)
}
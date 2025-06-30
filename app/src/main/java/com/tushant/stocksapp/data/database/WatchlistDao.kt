package com.tushant.stocksapp.data.database

import androidx.room.*
import com.tushant.stocksapp.data.database.entities.Watchlist
import com.tushant.stocksapp.data.database.entities.WatchlistStock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlists ORDER BY createdAt DESC")
    fun getAllWatchlists(): Flow<List<Watchlist>>

    @Query("SELECT * FROM watchlist_stocks WHERE watchlistId = :watchlistId")
    fun getWatchlistStocks(watchlistId: Long): Flow<List<WatchlistStock>>

    @Query("SELECT symbol FROM watchlist_stocks WHERE watchlistId = :watchlistId")
    suspend fun getStockSymbols(watchlistId: Long): List<String>

    @Query("SELECT COUNT(*) FROM watchlist_stocks WHERE watchlistId = :watchlistId")
    suspend fun getStockCount(watchlistId: Long): Int

    @Query("SELECT COUNT(*) > 0 FROM watchlist_stocks WHERE symbol = :symbol")
    suspend fun isStockInAnyWatchlist(symbol: String): Boolean

    @Insert
    suspend fun insertWatchlist(watchlist: Watchlist): Long

    @Insert
    suspend fun insertWatchlistStock(watchlistStock: WatchlistStock)

    @Delete
    suspend fun deleteWatchlist(watchlist: Watchlist)

    @Query("DELETE FROM watchlist_stocks WHERE watchlistId = :watchlistId AND symbol = :symbol")
    suspend fun removeStockFromWatchlist(watchlistId: Long, symbol: String)

    @Transaction
    suspend fun addStockToWatchlist(watchlistId: Long, symbol: String) {
        val existingStock = getWatchlistStocks(watchlistId).firstOrNull()?.find { it.symbol == symbol }
        if (existingStock == null) {
            insertWatchlistStock(WatchlistStock(watchlistId = watchlistId, symbol = symbol))
        }
    }
}
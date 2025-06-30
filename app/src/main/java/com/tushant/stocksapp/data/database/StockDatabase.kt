package com.tushant.stocksapp.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.tushant.stocksapp.data.database.entities.Watchlist
import com.tushant.stocksapp.data.database.entities.WatchlistStock

@Database(
    entities = [Watchlist::class, WatchlistStock::class],
    version = 1,
    exportSchema = false
)
abstract class StockDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao

    companion object {
        @Volatile
        private var INSTANCE: StockDatabase? = null

        fun getDatabase(context: Context): StockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockDatabase::class.java,
                    "stock_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
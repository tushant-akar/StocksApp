package com.tushant.stocksapp.di

import android.content.Context
import androidx.room.Room
import com.tushant.stocksapp.data.database.StockDatabase
import com.tushant.stocksapp.data.database.WatchlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideStockDatabase(@ApplicationContext context: Context): StockDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            StockDatabase::class.java,
            "stock_database"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideWatchlistDao(database: StockDatabase): WatchlistDao {
        return database.watchlistDao()
    }
}
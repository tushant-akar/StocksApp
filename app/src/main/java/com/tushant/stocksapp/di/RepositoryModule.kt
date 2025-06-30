package com.tushant.stocksapp.di

import com.tushant.stocksapp.data.api.StockApi
import com.tushant.stocksapp.data.database.WatchlistDao
import com.tushant.stocksapp.data.repository.StockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideStockRepository(
        api: StockApi,
        dao: WatchlistDao
    ): StockRepository {
        return StockRepository(api, dao)
    }
}
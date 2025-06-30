package com.tushant.stocksapp.ui.screens.watchlist

import com.tushant.stocksapp.data.models.StockItem

data class WatchlistDetailUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val stocksWithData: List<StockItem> = emptyList(),
    val averageChange: Double = 0.0,
    val error: String? = null
)
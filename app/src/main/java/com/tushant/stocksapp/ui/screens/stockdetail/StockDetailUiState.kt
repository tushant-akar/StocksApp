package com.tushant.stocksapp.ui.screens.stockdetail

import com.tushant.stocksapp.data.models.StockOverview

data class StockDetailUiState(
    val isLoading: Boolean = false,
    val stock: StockOverview? = null,
    val isInWatchlist: Boolean = false,
    val error: String? = null
)
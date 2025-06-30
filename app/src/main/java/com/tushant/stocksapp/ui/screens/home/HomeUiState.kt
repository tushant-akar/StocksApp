package com.tushant.stocksapp.ui.screens.home

import com.tushant.stocksapp.data.models.StockItem

data class HomeUiState(
    val isLoading: Boolean = false,
    val topGainers: List<StockItem> = emptyList(),
    val topLosers: List<StockItem> = emptyList(),
    val mostActive: List<StockItem> = emptyList(),
    val lastUpdated: String = "",
    val error: String? = null
) {
    val hasData: Boolean
        get() = topGainers.isNotEmpty() || topLosers.isNotEmpty() || mostActive.isNotEmpty()
}
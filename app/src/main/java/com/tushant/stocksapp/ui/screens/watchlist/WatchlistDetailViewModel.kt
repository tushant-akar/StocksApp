package com.tushant.stocksapp.ui.screens.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tushant.stocksapp.data.repository.StockRepository
import com.tushant.stocksapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistDetailViewModel @Inject constructor(
    private val repository: StockRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistDetailUiState())
    val uiState: StateFlow<WatchlistDetailUiState> = _uiState.asStateFlow()

    fun loadWatchlistStocks(watchlistId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val stockSymbols = repository.getStockSymbols(watchlistId)
                if (stockSymbols.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        stocksWithData = emptyList()
                    )
                    return@launch
                }

                repository.getTopGainersLosers().collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {}
                        is NetworkResult.Success -> {
                            result.data?.let { data ->
                                val allStocks = data.topGainers + data.topLosers + data.mostActive
                                val watchlistStocks = allStocks.filter { it.ticker in stockSymbols }

                                val avgChange = if (watchlistStocks.isNotEmpty()) {
                                    watchlistStocks.map { it.getChangePercentageAsDouble() }
                                        .average()
                                } else 0.0

                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    stocksWithData = watchlistStocks,
                                    averageChange = avgChange,
                                    error = null
                                )
                            }
                        }

                        is NetworkResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Failed to load stock data"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load watchlist: ${e.message}"
                )
            }
        }
    }

    fun refreshStockData() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        viewModelScope.launch {
            delay(1000)
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    fun removeStockFromWatchlist(watchlistId: Long, symbol: String) {
        viewModelScope.launch {
            try {
                repository.removeStockFromWatchlist(watchlistId, symbol)
                loadWatchlistStocks(watchlistId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to remove stock: ${e.message}"
                )
            }
        }
    }
}
package com.tushant.stocksapp.ui.screens.stockdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tushant.stocksapp.data.database.entities.Watchlist
import com.tushant.stocksapp.data.repository.StockRepository
import com.tushant.stocksapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockDetailViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockDetailUiState())
    val uiState: StateFlow<StockDetailUiState> = _uiState.asStateFlow()

    private val _watchlists = MutableStateFlow<List<Watchlist>>(emptyList())
    val watchlists: StateFlow<List<Watchlist>> = _watchlists.asStateFlow()

    init {
        loadWatchlists()
    }

    fun loadStockDetail(symbol: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                repository.getStockOverview(symbol).collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                        }

                        is NetworkResult.Success -> {
                            result.data?.let { stock ->
                                val isInWatchlist = repository.isStockInAnyWatchlist(symbol)
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    stock = stock,
                                    isInWatchlist = isInWatchlist,
                                    error = null
                                )
                            }
                        }

                        is NetworkResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = result.message ?: "Failed to load stock details"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network error: ${e.message}"
                )
            }
        }
    }

    private fun loadWatchlists() {
        viewModelScope.launch {
            repository.getAllWatchlists().collect { watchlists ->
                _watchlists.value = watchlists
            }
        }
    }

    fun addToWatchlist(watchlistId: Long, symbol: String) {
        viewModelScope.launch {
            try {
                repository.addStockToWatchlist(watchlistId, symbol)
                _uiState.value = _uiState.value.copy(isInWatchlist = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to add to watchlist: ${e.message}"
                )
            }
        }
    }

    fun createWatchlistAndAdd(name: String, symbol: String) {
        viewModelScope.launch {
            try {
                val watchlistId = repository.createWatchlist(name)
                repository.addStockToWatchlist(watchlistId, symbol)
                _uiState.value = _uiState.value.copy(isInWatchlist = true)
                loadWatchlists()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to create watchlist: ${e.message}"
                )
            }
        }
    }

    fun removeFromWatchlist(symbol: String) {
        viewModelScope.launch {
            try {
                _watchlists.value.forEach { watchlist ->
                    repository.removeStockFromWatchlist(watchlist.id, symbol)
                }
                _uiState.value = _uiState.value.copy(isInWatchlist = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to remove from watchlist: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
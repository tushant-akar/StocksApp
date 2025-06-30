package com.tushant.stocksapp.ui.screens.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tushant.stocksapp.data.database.entities.Watchlist
import com.tushant.stocksapp.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistUiState())
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    val watchlists: StateFlow<List<Watchlist>> = repository.getAllWatchlists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createWatchlist(name: String) {
        viewModelScope.launch {
            try {
                repository.createWatchlist(name)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to create watchlist: ${e.message}"
                )
            }
        }
    }

    fun deleteWatchlist(watchlist: Watchlist) {
        viewModelScope.launch {
            try {
                repository.deleteWatchlist(watchlist)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete watchlist: ${e.message}"
                )
            }
        }
    }

    fun getStockCount(watchlistId: Long, callback: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val count = repository.getStockCount(watchlistId)
                callback(count)
            } catch (_: Exception) {
                callback(0)
            }
        }
    }

}
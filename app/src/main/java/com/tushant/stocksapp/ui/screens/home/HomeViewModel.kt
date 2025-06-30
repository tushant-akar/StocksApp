package com.tushant.stocksapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tushant.stocksapp.data.repository.StockRepository
import com.tushant.stocksapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMarketData()
    }

    fun loadMarketData() {
        viewModelScope.launch {
            repository.getTopGainersLosers().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is NetworkResult.Success -> {
                        result.data?.let { data ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                topGainers = data.topGainers,
                                topLosers = data.topLosers,
                                mostActive = data.mostActive,
                                lastUpdated = data.lastUpdated,
                                error = null
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message ?: "Unknown error occurred"
                        )
                    }
                }
            }
        }
    }

    fun refresh() {
        loadMarketData()
    }
}
package com.tushant.stocksapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tushant.stocksapp.data.models.SymbolMatch
import com.tushant.stocksapp.data.preferences.PreferencesManager
import com.tushant.stocksapp.data.preferences.SearchStatistics
import com.tushant.stocksapp.data.repository.StockRepository
import com.tushant.stocksapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: StockRepository,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val recentSearches: StateFlow<List<String>> = preferencesManager.recentSearches
        .catch { exception ->
            emit(emptyList())
            _uiState.value = _uiState.value.copy(
                searchError = "Failed to load recent searches."
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val searchStatistics: StateFlow<SearchStatistics> = preferencesManager.searchStatistics
        .catch { exception ->
            emit(SearchStatistics())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchStatistics()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<SymbolMatch>> = _searchQuery
        .debounce(300)
        .filter { query -> query.trim().length >= 2 }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            performSearch(query)
        }
        .catch { exception ->
            _uiState.value = _uiState.value.copy(
                isSearching = false,
                searchError = "Search failed. Please try again."
            )
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.trim().isEmpty()) {
            _uiState.value = _uiState.value.copy(
                isSearching = false,
                searchError = null
            )
        }
    }

    private fun performSearch(query: String): Flow<List<SymbolMatch>> = flow {
        _uiState.value = _uiState.value.copy(
            isSearching = true,
            searchError = null
        )

        repository.searchSymbol(query).collect { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isSearching = true, searchError = null)
                }

                is NetworkResult.Success -> {
                    val matches = result.data?.bestMatches ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        searchError = null
                    )
                    emit(matches)
                }

                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        searchError = result.message ?: "Search failed"
                    )
                    emit(emptyList())
                }
            }
        }
    }

    fun addToRecentSearches(query: String) {
        viewModelScope.launch {
            try {
                preferencesManager.addRecentSearch(query)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchError = "Failed to add to recent searches"
                )
            }
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            try {
                preferencesManager.clearRecentSearches()
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchError = "Failed to clear recent searches"
                )
            }
        }
    }

    fun removeFromRecentSearches(query: String) {
        viewModelScope.launch {
            try {
                preferencesManager.removeRecentSearch(query)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchError = "Failed to remove recent search."
                )
            }
        }
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
        _uiState.value = _uiState.value.copy(
            isSearching = false,
            searchError = null
        )
    }

    fun retrySearch() {
        _searchQuery.value = ""
        _uiState.value = _uiState.value.copy(
            isSearching = false,
            searchError = null
        )
    }

}
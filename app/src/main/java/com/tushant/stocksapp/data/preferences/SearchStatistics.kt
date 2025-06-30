package com.tushant.stocksapp.data.preferences

import kotlinx.serialization.Serializable

@Serializable
data class SearchStatistics(
    val totalSearches: Int = 0,
    val mostCommonSearches: List<Pair<String, Int>> = emptyList()
)
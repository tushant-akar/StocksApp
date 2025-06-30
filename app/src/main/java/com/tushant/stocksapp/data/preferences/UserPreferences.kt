package com.tushant.stocksapp.data.preferences

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val isDarkTheme: Boolean = false,
)
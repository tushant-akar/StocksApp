package com.tushant.stocksapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val RECENT_SEARCHES = stringSetPreferencesKey("recent_searches")
        val SEARCH_STATISTICS = stringPreferencesKey("search_statistics")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            UserPreferences(
                isDarkTheme = preferences[PreferencesKeys.IS_DARK_THEME] ?: false
            )
        }

    val recentSearches: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.RECENT_SEARCHES]?.toList() ?: emptyList()
        }

    val searchStatistics: Flow<SearchStatistics> = context.dataStore.data
        .map { preferences ->
            val statisticsJson = preferences[PreferencesKeys.SEARCH_STATISTICS]
            if (statisticsJson != null) {
                try {
                    Json.decodeFromString<SearchStatistics>(statisticsJson)
                } catch (_: Exception) {
                    SearchStatistics()
                }
            } else {
                SearchStatistics()
            }
        }

    suspend fun updateDarkTheme(isDarkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDarkTheme
        }
    }

    suspend fun addRecentSearch(query: String) {
        context.dataStore.edit { preferences ->
            val currentSearches = preferences[PreferencesKeys.RECENT_SEARCHES]?.toMutableSet() ?: mutableSetOf()
            currentSearches.add(query)

            if (currentSearches.size > 20) {
                val recentList = currentSearches.toList()
                currentSearches.clear()
                currentSearches.addAll(recentList.takeLast(20))
            }

            preferences[PreferencesKeys.RECENT_SEARCHES] = currentSearches

            updateSearchStatisticsInternal(preferences, query)
        }
    }

    suspend fun removeRecentSearch(query: String) {
        context.dataStore.edit { preferences ->
            val currentSearches = preferences[PreferencesKeys.RECENT_SEARCHES]?.toMutableSet() ?: mutableSetOf()
            currentSearches.remove(query)
            preferences[PreferencesKeys.RECENT_SEARCHES] = currentSearches
        }
    }

    suspend fun clearRecentSearches() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.RECENT_SEARCHES)
        }
    }

    private fun updateSearchStatisticsInternal(preferences: androidx.datastore.preferences.core.MutablePreferences, query: String) {
        val currentStatsJson = preferences[PreferencesKeys.SEARCH_STATISTICS]
        val currentStats = if (currentStatsJson != null) {
            try {
                Json.decodeFromString<SearchStatistics>(currentStatsJson)
            } catch (_: Exception) {
                SearchStatistics()
            }
        } else {
            SearchStatistics()
        }

        val updatedSearches = currentStats.mostCommonSearches.toMutableList()
        val existingIndex = updatedSearches.indexOfFirst { it.first == query }

        if (existingIndex != -1) {
            val existing = updatedSearches[existingIndex]
            updatedSearches[existingIndex] = existing.copy(second = existing.second + 1)
        } else {
            updatedSearches.add(Pair(query, 1))
        }

        val sortedSearches = updatedSearches.sortedByDescending { it.second }.take(10)

        val updatedStats = currentStats.copy(
            totalSearches = currentStats.totalSearches + 1,
            mostCommonSearches = sortedSearches
        )

        preferences[PreferencesKeys.SEARCH_STATISTICS] = Json.encodeToString(updatedStats)
    }
}
package com.tushant.stocksapp.ui.screens.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tushant.stocksapp.data.database.entities.Watchlist
import com.tushant.stocksapp.data.models.StockItem
import com.tushant.stocksapp.data.preferences.PreferencesViewModel
import com.tushant.stocksapp.ui.components.EmptyState
import com.tushant.stocksapp.ui.components.ErrorState
import com.tushant.stocksapp.ui.components.LoadingState
import com.tushant.stocksapp.ui.components.StockCard
import com.tushant.stocksapp.ui.theme.Green
import kotlinx.coroutines.delay

@Composable
fun WatchlistDetailScreen(
    watchlistId: Long,
    onBackClick: () -> Unit,
    onStockClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    watchlistViewModel: WatchlistDetailViewModel = hiltViewModel(),
    preferencesViewModel: PreferencesViewModel = hiltViewModel(),
) {
    val uiState by watchlistViewModel.uiState.collectAsState()
    val userPreferences by preferencesViewModel.userPreferences.collectAsState()

    val watchlist by remember { mutableStateOf<Watchlist?>(null) }

    LaunchedEffect(watchlistId) {
        watchlistViewModel.loadWatchlistStocks(watchlistId)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30000)
            if (!uiState.isLoading) {
                watchlistViewModel.refreshStockData()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = watchlist?.name ?: "Watchlist",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )

                        if (uiState.stocksWithData.isNotEmpty()) {
                            Text(
                                text = "${uiState.stocksWithData.size} stocks",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = { watchlistViewModel.refreshStockData() },
                        enabled = !uiState.isRefreshing,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Green
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = Green,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { preferencesViewModel.toggleDarkTheme() },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(
                            imageVector = if (userPreferences.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (uiState.stocksWithData.isNotEmpty()) {
            item {
                WatchlistSummaryCard(
                    totalStocks = uiState.stocksWithData.size,
                    gainers = uiState.stocksWithData.count { it.getChangeAmountAsDouble() > 0 },
                    losers = uiState.stocksWithData.count { it.getChangeAmountAsDouble() < 0 },
                    avgChange = uiState.averageChange
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        when {
            uiState.isLoading && uiState.stocksWithData.isEmpty() -> {
                item {
                    LoadingState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        message = "Loading watchlist stocks..."
                    )
                }
            }

            uiState.error != null && uiState.stocksWithData.isEmpty() -> {
                item {
                    ErrorState(
                        message = uiState.error!!,
                        onRetry = { watchlistViewModel.loadWatchlistStocks(watchlistId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp)
                    )
                }
            }

            uiState.stocksWithData.isEmpty() -> {
                item {
                    EmptyState(
                        icon = "📈",
                        title = "No stocks added yet",
                        description = "Add stocks from the home screen to track them here",
                        actionText = "Go Home",
                        onAction = onBackClick,
                        modifier = Modifier.padding(vertical = 60.dp)
                    )
                }
            }

            else -> {
                items(
                    items = uiState.stocksWithData,
                    key = { it.ticker }
                ) { stockItem ->
                    StockItemRow(
                        stockItem = stockItem,
                        onStockClick = { onStockClick(stockItem.ticker) },
                        onRemoveClick = {
                            watchlistViewModel.removeStockFromWatchlist(watchlistId, stockItem.ticker)
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun WatchlistSummaryCard(
    totalStocks: Int,
    gainers: Int,
    losers: Int,
    avgChange: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (MaterialTheme.colorScheme.background == Color.Black) 0.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(
                label = "Total",
                value = totalStocks.toString(),
                icon = "📊"
            )
            SummaryItem(
                label = "Gainers",
                value = gainers.toString(),
                icon = "📈",
                valueColor = Green
            )
            SummaryItem(
                label = "Losers",
                value = losers.toString(),
                icon = "📉",
                valueColor = MaterialTheme.colorScheme.error
            )
            SummaryItem(
                label = "Avg Change",
                value = "${if (avgChange >= 0) "+" else ""}${String.format("%.1f", avgChange)}%",
                icon = if (avgChange >= 0) "⬆️" else "⬇️",
                valueColor = if (avgChange >= 0) Green else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    icon: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun StockItemRow(
    stockItem: StockItem,
    onStockClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StockCard(
            stock = stockItem,
            onClick = { onStockClick() },
            modifier = Modifier.weight(1f),
            compact = true
        )

        IconButton(
            onClick = { showDeleteDialog = true }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove from watchlist",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Remove Stock")
            },
            text = {
                Text("Remove ${stockItem.ticker} from this watchlist?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemoveClick()
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        "Remove",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
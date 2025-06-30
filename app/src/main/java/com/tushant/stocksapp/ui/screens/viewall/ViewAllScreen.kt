package com.tushant.stocksapp.ui.screens.viewall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tushant.stocksapp.ui.components.EmptyState
import com.tushant.stocksapp.ui.components.ErrorState
import com.tushant.stocksapp.ui.components.LoadingState
import com.tushant.stocksapp.ui.components.StockCard
import com.tushant.stocksapp.ui.screens.home.HomeViewModel

@Composable
fun ViewAllScreen(
    section: String,
    onBackClick: () -> Unit,
    onStockClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    val title = when (section) {
        "gainers" -> "Top Gainers"
        "losers" -> "Top Losers"
        "active" -> "Most Active"
        else -> "Stocks"
    }

    val stocks = when (section) {
        "gainers" -> uiState.topGainers
        "losers" -> uiState.topLosers
        "active" -> uiState.mostActive
        else -> emptyList()
    }

    LaunchedEffect(Unit) {
        if (!uiState.hasData && !uiState.isLoading) {
            homeViewModel.loadMarketData()
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
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        when {
            uiState.isLoading -> {
                item {
                    LoadingState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                    )
                }
            }

            uiState.error != null -> {
                item {
                    ErrorState(
                        message = uiState.error!!,
                        onRetry = { homeViewModel.refresh() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp)
                    )
                }
            }

            stocks.isEmpty() && uiState.hasData -> {
                item {
                    EmptyState(
                        icon = "📊",
                        title = "No stocks available",
                        description = "Unable to load stocks for this section",
                        modifier = Modifier.padding(vertical = 40.dp)
                    )
                }
            }

            else -> {
                items(stocks) { stock ->
                    StockCard(
                        stock = stock,
                        onClick = { onStockClick(stock.ticker) },
                        compact = true
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
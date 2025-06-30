package com.tushant.stocksapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tushant.stocksapp.data.models.StockItem
import com.tushant.stocksapp.data.preferences.PreferencesViewModel
import com.tushant.stocksapp.ui.components.EmptyState
import com.tushant.stocksapp.ui.components.ErrorState
import com.tushant.stocksapp.ui.components.LoadingState
import com.tushant.stocksapp.ui.components.StockCard
import com.tushant.stocksapp.ui.theme.Green

@Composable
fun HomeScreen(
    onStockClick: (String) -> Unit,
    onViewAllClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val userPreferences by preferencesViewModel.userPreferences.collectAsState()

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
                Text(
                    text = "Stocks",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default,
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(
                    onClick = { preferencesViewModel.toggleDarkTheme() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = if (userPreferences.isDarkTheme) "☀️" else "🌙",
                        fontSize = 20.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            OutlinedTextField(
                value = "",
                onValueChange = { },
                placeholder = {
                    Text(
                        text = "Search stocks...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Default
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSearchClick() },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                enabled = false,
                readOnly = true
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        when {
            uiState.isLoading -> {
                item {
                    LoadingState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp)
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

            uiState.hasData -> {
                if (uiState.topGainers.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Top Gainers",
                            onViewAll = { onViewAllClick("gainers") }
                        )
                    }

                    items(uiState.topGainers.take(2)) { stock ->
                        StockCard(
                            stock = stock,
                            onClick = { onStockClick(stock.ticker) },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (uiState.topLosers.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Top Losers",
                            onViewAll = { onViewAllClick("losers") }
                        )
                    }

                    items(uiState.topLosers.take(2)) { stock ->
                        StockCard(
                            stock = stock,
                            onClick = { onStockClick(stock.ticker) },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (uiState.mostActive.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Most Active",
                            onViewAll = { onViewAllClick("active") }
                        )
                    }

                    items(uiState.mostActive.take(2)) { stock ->
                        StockCard(
                            stock = stock,
                            onClick = { onStockClick(stock.ticker) },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }

            else -> {
                item {
                    EmptyState(
                        icon = "📈",
                        title = "No Data Available",
                        description = "Unable to load market data at the moment",
                        actionText = "Retry",
                        onAction = { homeViewModel.refresh() }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(150.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Default,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextButton(onClick = onViewAll) {
            Text(
                text = "View All",
                color = Green,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Default
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}
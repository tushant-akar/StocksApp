package com.tushant.stocksapp.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tushant.stocksapp.data.models.SymbolMatch
import com.tushant.stocksapp.ui.components.EmptyState
import com.tushant.stocksapp.ui.components.ErrorState
import com.tushant.stocksapp.ui.theme.Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onStockClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val searchStatistics by viewModel.searchStatistics.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        keyboardController?.hide()
                        onBackClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "Search stocks, ETFs...",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        if (uiState.isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Green
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.clearSearchQuery() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when {
                uiState.searchError != null && !uiState.isSearching -> {
                    item {
                        ErrorState(
                            message = uiState.searchError!!,
                            onRetry = { viewModel.retrySearch() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp)
                        )
                    }
                }

                searchQuery.trim().length >= 2 && searchResults.isNotEmpty() -> {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Search Results",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            Text(
                                text = "${searchResults.size} found",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(searchResults) { symbolMatch ->
                        SearchResultItem(
                            symbolMatch = symbolMatch,
                            onClick = {
                                viewModel.addToRecentSearches(symbolMatch.symbol)
                                onStockClick(symbolMatch.symbol)
                            }
                        )
                    }
                }

                searchQuery.trim().length >= 2 && searchResults.isEmpty() && !uiState.isSearching -> {
                    item {
                        EmptyState(
                            icon = "🔍",
                            title = "No stocks found",
                            description = "Try searching with a different keyword or stock symbol",
                            modifier = Modifier.padding(vertical = 40.dp)
                        )
                    }
                }

                else -> {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (recentSearches.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recent Searches",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold
                                )

                                TextButton(
                                    onClick = { viewModel.clearRecentSearches() }
                                ) {
                                    Text(
                                        text = "Clear All",
                                        color = Green,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(recentSearches.take(10)) { query ->
                            RecentSearchItem(
                                query = query,
                                onClick = {
                                    viewModel.updateSearchQuery(query)
                                },
                                onRemove = {
                                    viewModel.removeFromRecentSearches(query)
                                }
                            )
                        }
                    }

                    if (recentSearches.isEmpty()) {
                        item {
                            EmptyState(
                                icon = "🔍",
                                title = "Start Searching",
                                description = "Search for stocks, ETFs, and other securities",
                                modifier = Modifier.padding(vertical = 60.dp)
                            )
                        }

                        item {
                            Text(
                                text = "Popular Stocks",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        val popularStocks = listOf(
                            "AAPL" to "Apple Inc.",
                            "GOOGL" to "Alphabet Inc.",
                            "MSFT" to "Microsoft Corp.",
                            "TSLA" to "Tesla Inc.",
                            "AMZN" to "Amazon.com Inc.",
                            "NVDA" to "NVIDIA Corp."
                        )

                        items(popularStocks) { (symbol, name) ->
                            PopularStockItem(
                                symbol = symbol,
                                name = name,
                                onClick = {
                                    viewModel.addToRecentSearches(symbol)
                                    onStockClick(symbol)
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    symbolMatch: SymbolMatch,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Green.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbolMatch.symbol.take(2),
                    color = Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = symbolMatch.symbol,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = symbolMatch.name,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (symbolMatch.region.isNotEmpty()) {
                    Text(
                        text = "${symbolMatch.region} • ${symbolMatch.currency}",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = "📈",
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun RecentSearchItem(
    query: String,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = query,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


@Composable
private fun PopularStockItem(
    symbol: String,
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Green.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol.take(2),
                    color = Green,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = symbol,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                color = Green.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "Popular",
                    color = Green,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
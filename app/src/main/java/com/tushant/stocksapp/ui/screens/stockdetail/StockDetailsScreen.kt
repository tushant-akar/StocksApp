package com.tushant.stocksapp.ui.screens.stockdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tushant.stocksapp.data.models.StockItem
import com.tushant.stocksapp.data.models.StockOverview
import com.tushant.stocksapp.ui.components.ErrorState
import com.tushant.stocksapp.ui.components.LoadingState
import com.tushant.stocksapp.ui.components.WatchlistBottomSheet
import com.tushant.stocksapp.ui.screens.home.HomeViewModel
import com.tushant.stocksapp.ui.theme.Green
import com.tushant.stocksapp.ui.theme.LossRed
import com.tushant.stocksapp.ui.theme.ProfitGreen
import com.tushant.stocksapp.utils.formatCurrency
import com.tushant.stocksapp.utils.formatPercentage
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    stockTicker: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StockDetailViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val homeUiState by homeViewModel.uiState.collectAsState()
    val watchlists by viewModel.watchlists.collectAsState()

    var selectedTab by remember { mutableStateOf("Overview") }
    var showWatchlistSheet by remember { mutableStateOf(false) }
    var newWatchlistName by remember { mutableStateOf("") }

    val stockItem = remember(stockTicker, homeUiState) {
        val allStocks = homeUiState.topGainers + homeUiState.topLosers + homeUiState.mostActive
        allStocks.find { it.ticker == stockTicker }
    }

    LaunchedEffect(stockTicker) {
        viewModel.loadStockDetail(stockTicker)
        if (!homeUiState.hasData) {
            homeViewModel.loadMarketData()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
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
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { showWatchlistSheet = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = if (uiState.isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Watchlist",
                        tint = if (uiState.isInWatchlist) Green else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        when {
            uiState.isLoading -> {
                LoadingState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            uiState.error != null -> {
                ErrorState(
                    message = uiState.error!!,
                    onRetry = { viewModel.loadStockDetail(stockTicker) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            uiState.stock != null -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    item {
                        StockHeader(
                            stock = uiState.stock!!,
                            stockItem = stockItem,
                            stockTicker = stockTicker
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        ChartCard()
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        FundamentalsCard(stock = uiState.stock!!)
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        TabRow(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        when (selectedTab) {
                            "Overview" -> OverviewTab(
                                stock = uiState.stock!!
                            )

                            "Events" -> EventsTab(
                                stock = uiState.stock!!
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    if (showWatchlistSheet) {
        ModalBottomSheet(
            onDismissRequest = { showWatchlistSheet = false }
        ) {
            WatchlistBottomSheet(
                watchlists = watchlists,
                newWatchlistName = newWatchlistName,
                onNewWatchlistNameChange = { newWatchlistName = it },
                onWatchlistSelected = { watchlistId ->
                    viewModel.addToWatchlist(watchlistId, stockTicker)
                    showWatchlistSheet = false
                },
                onCreateWatchlist = {
                    if (newWatchlistName.isNotBlank()) {
                        viewModel.createWatchlistAndAdd(newWatchlistName, stockTicker)
                        newWatchlistName = ""
                        showWatchlistSheet = false
                    }
                },
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Composable
private fun StockHeader(
    stock: StockOverview,
    stockItem: StockItem?,
    stockTicker: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Green.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📈",
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stock.name.ifEmpty { stockTicker },
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (stockItem != null) {
            Text(
                text = stockItem.getPriceAsDouble().formatCurrency(),
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val isPositive = stockItem.isPositiveChange()
            val changeColor = if (isPositive) ProfitGreen else LossRed

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isPositive) "+" else ""}${
                        abs(stockItem.getChangeAmountAsDouble()).formatCurrency()
                    }",
                    color = changeColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "(${if (isPositive) "+" else ""}${
                        stockItem.getChangePercentageAsDouble().formatPercentage()
                    })",
                    color = changeColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "1D",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        } else {
            Text(
                text = "Loading price...",
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = Green.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "⛓️ Option Chain",
                color = Green,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ChartCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Price Chart",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("1D", "1W", "1M", "3M", "1Y").forEach { period ->
                        Surface(
                            color = if (period == "1D") Green else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = period,
                                color = if (period == "1D") Color.White else MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.6f
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📈",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Chart will be displayed here",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FundamentalsCard(stock: StockOverview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Fundamentals",
                fontSize = 18.sp,
                color = Green,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    MetricRow("Mkt Cap", stock.getFormattedMarketCap())
                    MetricRow("ROE", stock.getFormattedRoe())
                    MetricRow(
                        "P/B Ratio",
                        if (stock.priceToBookRatio.isNotEmpty()) stock.priceToBookRatio else "N/A"
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    MetricRow("Div Yield", stock.getFormattedDividendYield())
                    MetricRow(
                        "Book Value",
                        if (stock.bookValue.isNotEmpty()) stock.bookValue else "N/A"
                    )
                    MetricRow("Debt to Equity", stock.getFormattedPeRatio())
                }
            }
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TabRow(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
) {
    val tabs = listOf("Overview", "Events")

    androidx.compose.material3.TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        containerColor = Color.Transparent,
        indicator = { tabPositions ->
            if (tabs.indexOf(selectedTab) != -1) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(selectedTab)]),
                    color = Green
                )
            }
        }
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab,
                        color = if (selectedTab == tab) Green else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.6f
                        ),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    }
}

@Composable
private fun OverviewTab(
    stock: StockOverview,
) {
    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Performance",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ℹ️",
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Today's low",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${stock.dayMovingAverage50.ifEmpty { "N/A" }}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Today's high",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${stock.dayMovingAverage200.ifEmpty { "N/A" }}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        if (stock.description.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "About ${stock.symbol}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stock.description,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    if (stock.sector.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            color = Green.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = stock.sector,
                                color = Green,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventsTab(
    stock: StockOverview,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Upcoming Events",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Placeholder events - in real app these would come from API
            EventRow(
                icon = "💰",
                title = "Q1 FY26 Earnings Result",
                description = "Quarterly earnings announcement expected",
                date = "2025-07-15",
                impact = "high"
            )

            EventRow(
                icon = "⭐",
                title = "Dividend Declaration",
                description = "Board meeting for dividend declaration",
                date = "2025-07-20",
                impact = "medium"
            )

            EventRow(
                icon = "👥",
                title = "Annual General Meeting",
                description = "Annual shareholder meeting",
                date = "2025-08-05",
                impact = "low"
            )
        }
    }
}

@Composable
private fun EventRow(
    icon: String,
    title: String,
    description: String,
    date: String,
    impact: String,
) {
    val impactColor = when (impact) {
        "high" -> LossRed
        "medium" -> Color(0xFFFFA500)
        "low" -> Green
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(impactColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    color = impactColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = impact.uppercase(),
                        color = impactColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅",
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = date,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}
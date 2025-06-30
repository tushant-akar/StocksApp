package com.tushant.stocksapp.ui.navigation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tushant.stocksapp.data.database.entities.Watchlist
import com.tushant.stocksapp.data.models.StockItem
import com.tushant.stocksapp.data.preferences.PreferencesManager
import com.tushant.stocksapp.data.preferences.PreferencesViewModel
import com.tushant.stocksapp.ui.screens.home.HomeScreen
import com.tushant.stocksapp.ui.screens.search.SearchScreen
import com.tushant.stocksapp.ui.screens.stockdetail.StockDetailScreen
import com.tushant.stocksapp.ui.screens.viewall.ViewAllScreen
import com.tushant.stocksapp.ui.screens.watchlist.WatchlistDetailScreen
import com.tushant.stocksapp.ui.screens.watchlist.WatchlistScreen
import com.tushant.stocksapp.ui.theme.Green
import com.tushant.stocksapp.ui.theme.StocksAppTheme

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    preferencesViewModel: PreferencesViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val userPreferences by preferencesViewModel.userPreferences.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        NavigationItem.Home,
        NavigationItem.Watchlist
    )

    StocksAppTheme(darkTheme = userPreferences.isDarkTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background
                )
        ) {
            NavHost(
                navController = navController,
                startDestination = NavigationItem.Home.route,
                modifier = modifier.fillMaxSize()
            ) {
                composable(NavigationItem.Home.route) {
                    HomeScreen(
                        onStockClick = { stockTicker ->
                            navController.navigate("stock_detail/$stockTicker")
                        },
                        onViewAllClick = { section ->
                            navController.navigate("view_all/$section")
                        },
                        onSearchClick = {
                            navController.navigate("search")
                        }
                    )
                }

                composable("search") {
                    SearchScreen(
                        onBackClick = { navController.popBackStack() },
                        onStockClick = { stockTicker ->
                            navController.navigate("stock_detail/$stockTicker") {
                                popUpTo("search") { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavigationItem.Watchlist.route) {
                    WatchlistScreen(
                        onWatchlistClick = { watchlistId ->
                            navController.navigate("watchlist_detail/$watchlistId")
                        }
                    )
                }

                composable(
                    route = "stock_detail/{stockTicker}",
                    arguments = listOf(navArgument("stockTicker") { type = NavType.StringType })
                ) { backStackEntry ->
                    val stockTicker = backStackEntry.arguments?.getString("stockTicker") ?: ""
                    StockDetailScreen(
                        stockTicker = stockTicker,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = "view_all/{section}",
                    arguments = listOf(navArgument("section") { type = NavType.StringType })
                ) { backStackEntry ->
                    val section = backStackEntry.arguments?.getString("section") ?: ""
                    ViewAllScreen(
                        section = section,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onStockClick = { stockTicker ->
                            navController.navigate("stock_detail/$stockTicker")
                        }
                    )
                }

                composable(
                    route = "watchlist_detail/{watchlistId}",
                    arguments = listOf(navArgument("watchlistId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val watchlistId = backStackEntry.arguments?.getLong("watchlistId") ?: 0L
                    WatchlistDetailScreen(
                        watchlistId = watchlistId,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onStockClick = { stockTicker ->
                            navController.navigate("stock_detail/$stockTicker")
                        }
                    )
                }
            }

            val showBottomNav = currentRoute in bottomNavItems.map { it.route }
            if (showBottomNav) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            bottomNavItems.forEach { item ->
                                BottomNavItem(
                                    item = item,
                                    selected = currentRoute == item.route,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    item: NavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) Green.copy(alpha = 0.1f) else Color.Transparent
    val contentColor =
        if (selected) Green else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = modifier.padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = if (selected) item.iconSelected else item.iconUnselected,
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.label,
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
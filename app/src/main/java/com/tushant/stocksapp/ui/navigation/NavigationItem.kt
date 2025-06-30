package com.tushant.stocksapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val label: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
) {
    object Home: NavigationItem(
        route = "home",
        label = "Home",
        iconSelected = Icons.Filled.Home,
        iconUnselected = Icons.Outlined.Home
    )

    object Watchlist: NavigationItem(
        route = "watchlist",
        label = "Watchlist",
        iconSelected = Icons.AutoMirrored.Filled.List,
        iconUnselected = Icons.AutoMirrored.Outlined.List
    )
}
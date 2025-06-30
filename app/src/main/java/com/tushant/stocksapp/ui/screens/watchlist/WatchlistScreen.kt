package com.tushant.stocksapp.ui.screens.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tushant.stocksapp.data.database.entities.Watchlist
import com.tushant.stocksapp.data.preferences.PreferencesViewModel
import com.tushant.stocksapp.ui.components.EmptyState
import com.tushant.stocksapp.ui.theme.Green
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WatchlistScreen(
    onWatchlistClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    watchlistViewModel: WatchlistViewModel = hiltViewModel(),
    preferencesViewModel: PreferencesViewModel = hiltViewModel(),
) {
    val watchlists by watchlistViewModel.watchlists.collectAsState()
    val userPreferences by preferencesViewModel.userPreferences.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newWatchlistName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Watchlist",
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
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
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (watchlists.isEmpty()) {
            item {
                EmptyState(
                    icon = "📊",
                    title = "No watchlists yet",
                    description = "Create your first watchlist to track your favorite stocks",
                    actionText = "Create Watchlist",
                    onAction = { showCreateDialog = true },
                    modifier = Modifier.padding(vertical = 40.dp)
                )
            }
        } else {
            items(watchlists) { watchlist ->
                WatchlistCard(
                    watchlist = watchlist,
                    onClick = { onWatchlistClick(watchlist.id) },
                    onDelete = { watchlistViewModel.deleteWatchlist(watchlist) },
                    getStockCount = { callback ->
                        watchlistViewModel.getStockCount(watchlist.id, callback)
                    },
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                OutlinedButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Green
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create New Watchlist",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(150.dp))
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
                newWatchlistName = ""
            },
            title = {
                Text(
                    text = "Create Watchlist",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = newWatchlistName,
                    onValueChange = { newWatchlistName = it },
                    placeholder = { Text("Enter watchlist name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newWatchlistName.isNotBlank()) {
                            watchlistViewModel.createWatchlist(newWatchlistName.trim())
                            newWatchlistName = ""
                            showCreateDialog = false
                        }
                    },
                    enabled = newWatchlistName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green
                    )
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateDialog = false
                        newWatchlistName = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun WatchlistCard(
    watchlist: Watchlist,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    getStockCount: ((Int) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    var stockCount by remember { mutableStateOf(0) }

    LaunchedEffect(watchlist.id) {
        getStockCount { count ->
            stockCount = count
        }
    }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val createdDate = dateFormat.format(Date(watchlist.createdAt))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = watchlist.name,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$stockCount stocks • Created $createdDate",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(
                        text = "🗑️",
                        fontSize = 18.sp
                    )
                }

                Text(
                    text = "→",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 18.sp
                )
            }
        }
    }
}
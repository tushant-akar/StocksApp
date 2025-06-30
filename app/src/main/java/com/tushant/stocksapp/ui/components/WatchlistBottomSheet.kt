package com.tushant.stocksapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tushant.stocksapp.data.database.entities.Watchlist
import com.tushant.stocksapp.ui.theme.Green

@Composable
fun WatchlistBottomSheet(
    watchlists: List<Watchlist>,
    newWatchlistName: String,
    onNewWatchlistNameChange: (String) -> Unit,
    onWatchlistSelected: (Long) -> Unit,
    onCreateWatchlist: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Text(
                text = "Add to Watchlist",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }

        if (watchlists.isNotEmpty()) {
            items(watchlists) { watchlist ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onWatchlistSelected(watchlist.id) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = watchlist.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "${watchlist.id} stocks",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
        }

        item {
            Text(
                text = "Or create new watchlist:",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 14.sp,
            )
        }

        item {
            OutlinedTextField(
                value = newWatchlistName,
                onValueChange = onNewWatchlistNameChange,
                placeholder = { Text("Enter watchlist name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Button(
                onClick = onCreateWatchlist,
                enabled = newWatchlistName.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Create & Add",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
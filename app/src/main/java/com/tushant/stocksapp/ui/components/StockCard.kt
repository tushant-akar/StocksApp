package com.tushant.stocksapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tushant.stocksapp.data.models.StockItem
import com.tushant.stocksapp.ui.theme.Green
import com.tushant.stocksapp.ui.theme.LossRed
import com.tushant.stocksapp.ui.theme.ProfitGreen
import com.tushant.stocksapp.utils.formatCurrency
import com.tushant.stocksapp.utils.formatPercentage

@Composable
fun StockCard(
    stock: StockItem,
    onClick: (StockItem) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val isPositive = stock.isPositiveChange()
    val changeColor = if (isPositive) ProfitGreen else LossRed

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(stock) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (MaterialTheme.colorScheme.background == Color.Black) 0.dp else 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(if (compact) 16.dp else 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(if (compact) 36.dp else 40.dp)
                        .clip(CircleShape)
                        .background(color = Green.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = Green,
                        modifier = Modifier.size(if (compact) 16.dp else 18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stock.ticker,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = if (compact) 15.sp else 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Default,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Vol: ${stock.getFormattedVolume()}",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = if (compact) 11.sp else 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = "Trend",
                    tint = changeColor,
                    modifier = Modifier.size(if (compact) 18.dp else 20.dp) // TSX trend icon sizes
                )
            }

            Spacer(modifier = Modifier.height(if (compact) 12.dp else 16.dp))

            Text(
                text = stock.getPriceAsDouble().formatCurrency(),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = if (compact) 20.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = changeColor.copy(alpha = 0.2f),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = "${if (isPositive) "+" else ""}${stock.getChangePercentageAsDouble().formatPercentage()}",
                        color = changeColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Default,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "${if (isPositive) "+" else ""}${stock.getChangePercentageAsDouble().formatCurrency()}",
                    color = changeColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Default
                )
            }
        }
    }
}
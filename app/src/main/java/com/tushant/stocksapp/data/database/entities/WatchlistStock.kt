package com.tushant.stocksapp.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watchlist_stocks",
    foreignKeys = [
        ForeignKey(
            entity = Watchlist::class,
            parentColumns = ["id"],
            childColumns = ["watchlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["watchlistId"])]
)
data class WatchlistStock(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val watchlistId: Long,
    val symbol: String,
    val addedAt: Long = System.currentTimeMillis()
)
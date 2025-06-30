package com.tushant.stocksapp.data.models

import com.google.gson.annotations.SerializedName

data class TopGainersLosersResponse(
    @SerializedName("metadata") val metadata: String = "",
    @SerializedName("last_updated") val lastUpdated: String = "",
    @SerializedName("top_gainers") val topGainers: List<StockItem> = emptyList(),
    @SerializedName("top_losers") val topLosers: List<StockItem> = emptyList(),
    @SerializedName("most_actively_traded") val mostActive: List<StockItem> = emptyList()
)
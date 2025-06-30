package com.tushant.stocksapp.data.models

import com.google.gson.annotations.SerializedName

data class StockItem(
    @SerializedName("ticker") val ticker: String = "",
    @SerializedName("price") val price: String = "",
    @SerializedName("change_amount") val changeAmount: String = "",
    @SerializedName("change_percentage") val changePercentage: String = "",
    @SerializedName("volume") val volume: String = ""
) {
    fun getPriceAsDouble(): Double = price.toDoubleOrNull() ?: 0.0
    fun getChangeAmountAsDouble(): Double = changeAmount.toDoubleOrNull() ?: 0.0
    fun getChangePercentageAsDouble(): Double {
        return changePercentage.replace("%", "").toDoubleOrNull() ?: 0.0
    }
    fun getVolumeAsLong(): Long = volume.toLongOrNull() ?: 0L
    fun isPositiveChange(): Boolean = getChangeAmountAsDouble() >= 0

    fun getFormattedVolume(): String {
        val vol = getVolumeAsLong()
        return when {
            vol >= 1_000_000_000 -> String.format("%.1fB", vol / 1_000_000_000.0)
            vol >= 1_000_000 -> String.format("%.1fM", vol / 1_000_000.0)
            vol >= 1_000 -> String.format("%.1fK", vol / 1_000.0)
            else -> vol.toString()
        }
    }
}
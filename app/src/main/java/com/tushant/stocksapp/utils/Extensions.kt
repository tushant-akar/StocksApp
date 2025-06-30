package com.tushant.stocksapp.utils

import androidx.compose.ui.graphics.Color
import java.text.NumberFormat
import java.util.*

fun Double.formatCurrency(): String {
    return NumberFormat.getCurrencyInstance(Locale("en", "US")).format(this)
}

fun Double.formatPercentage(): String {
    return String.format("%.2f%%", this)
}

fun Long.formatNumber(): String {
    return NumberFormat.getNumberInstance(Locale.getDefault()).format(this)
}

fun String.toSafeDouble(): Double {
    return this.toDoubleOrNull() ?: 0.0
}

fun Color.Companion.fromHex(colorString: String): Color {
    return Color(android.graphics.Color.parseColor(colorString))
}
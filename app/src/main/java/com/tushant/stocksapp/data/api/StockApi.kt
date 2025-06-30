package com.tushant.stocksapp.data.api

import com.tushant.stocksapp.data.models.StockOverview
import com.tushant.stocksapp.data.models.SymbolSearchResponse
import com.tushant.stocksapp.data.models.TopGainersLosersResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    @GET("query")
    suspend fun getTopGainersLosers(
        @Query("function") function: String = "TOP_GAINERS_LOSERS",
        @Query("apikey") apiKey: String
    ): Response<TopGainersLosersResponse>

    @GET("query")
    suspend fun getStockOverview(
        @Query("function") function: String = "OVERVIEW",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): Response<StockOverview>

    @GET("query")
    suspend fun searchSymbol(
        @Query("function") function: String = "SYMBOL_SEARCH",
        @Query("keywords") keywords: String,
        @Query("apikey") apiKey: String
    ): Response<SymbolSearchResponse>
}
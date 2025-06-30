package com.tushant.stocksapp.di

import android.content.Context
import com.tushant.stocksapp.data.api.StockApi
import com.tushant.stocksapp.utils.Constants
import com.tushant.stocksapp.utils.Constants.BASE_URL
import com.tushant.stocksapp.utils.Constants.CACHE_MAX_AGE
import com.tushant.stocksapp.utils.Constants.CACHE_MAX_STALE
import com.tushant.stocksapp.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val cacheSize = Constants.CACHE_SIZE
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)

        val cacheInterceptor = Interceptor { chain ->
            val request = chain.request()
            val cacheControl = CacheControl.Builder()
                .maxAge(CACHE_MAX_AGE, TimeUnit.SECONDS)
                .build()

            val response = chain.proceed(request)
            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }

        val offlineCacheInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (!NetworkUtils.isNetworkAvailable(context)) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(CACHE_MAX_STALE, TimeUnit.DAYS)
                    .onlyIfCached()
                    .build()

                request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(cacheInterceptor)
            .addNetworkInterceptor(offlineCacheInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): StockApi {
        return retrofit.create(StockApi::class.java)
    }
}
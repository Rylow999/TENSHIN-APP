package com.tenshin.app.di

import android.content.Context
import com.tenshin.app.data.remote.ApiService
import com.tenshin.app.data.remote.AutoConfig
import com.tenshin.app.data.remote.WarframeMarketApi
import com.tenshin.app.data.remote.WarframeHelperApi
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val PLACEHOLDER_URL = "https://jsonplaceholder.typicode.com/"
    private const val WFM_API_URL = "https://api.warframe.market/v1/"
    
    @Volatile
    private var helperBaseUrl: String? = null
    @Volatile
    private var helperIp: String? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        createRetrofit(PLACEHOLDER_URL).create(ApiService::class.java)
    }

    val warframeMarketApi: WarframeMarketApi by lazy {
        createRetrofit(WFM_API_URL).create(WarframeMarketApi::class.java)
    }

    fun setHelperIp(ip: String) {
        helperIp = ip
        helperBaseUrl = "http://$ip:8080/"
    }

    fun initializeWithInjectedIp(context: Context) {
        AutoConfig.getInjectedIp(context)?.let { ip ->
            setHelperIp(ip)
        }
    }

    fun getHelperIp(): String? = helperIp

    fun getWarframeHelperApi(): WarframeHelperApi? {
        val url = helperBaseUrl ?: return null
        return createRetrofit(url).create(WarframeHelperApi::class.java)
    }

    fun createWebSocket(listener: WebSocketListener): WebSocket? {
        val ip = helperIp ?: return null
        val request = Request.Builder()
            .url("ws://$ip:8080/ws")
            .build()
        return okHttpClient.newWebSocket(request, listener)
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

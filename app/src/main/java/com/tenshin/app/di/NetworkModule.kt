package com.tenshin.app.di

import android.content.Context
import com.tenshin.app.data.remote.*
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
    private const val WF_STAT_URL = "https://api.warframestat.us/"
    private const val GROQ_API_URL = "https://api.groq.com/openai/"
    
    @Volatile
    private var helperIp: String? = null
    @Volatile
    private var httpPort: Int = 8080
    @Volatile
    private var wsPort: Int = 8081

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS) 
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        createRetrofit(PLACEHOLDER_URL).create(ApiService::class.java)
    }

    val warframeMarketApi: WarframeMarketApi by lazy {
        createRetrofit(WFM_API_URL).create(WarframeMarketApi::class.java)
    }

    val warframeStatApi: WarframeStatApi by lazy {
        createRetrofit(WF_STAT_URL).create(WarframeStatApi::class.java)
    }

    val groqApi: GroqApi by lazy {
        createRetrofit(GROQ_API_URL).create(GroqApi::class.java)
    }

    fun setHelperConfig(ip: String, httpPort: Int = 8080, wsPort: Int = 8081) {
        this.helperIp = ip
        this.httpPort = httpPort
        this.wsPort = wsPort
    }

    fun initializeWithInjectedIp(context: Context) {
        AutoConfig.getInjectedIp(context)?.let { ip ->
            setHelperConfig(ip)
        }
    }

    fun getHelperIp(): String? = helperIp

    fun getWarframeHelperApi(): WarframeHelperApi? {
        val ip = helperIp ?: return null
        val url = "http://$ip:$httpPort/"
        return createRetrofit(url).create(WarframeHelperApi::class.java)
    }

    fun createWebSocket(listener: WebSocketListener): WebSocket? {
        val ip = helperIp ?: return null
        val request = Request.Builder()
            .url("ws://$ip:$wsPort/ws")
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

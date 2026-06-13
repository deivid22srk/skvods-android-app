package com.deivid22srk.skvodsapp.network

import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface SkvodsApi {
    @GET("api/{path}")
    suspend fun getApiData(@Path("path", encoded = true) path: String): retrofit2.Response<JsonElement>

    @GET("data/{path}")
    suspend fun getData(@Path("path", encoded = true) path: String): retrofit2.Response<JsonElement>

    @GET("test_streams/{path}")
    suspend fun getStreamMeta(@Path("path", encoded = true) path: String): retrofit2.Response<JsonElement>
}

object ApiClient {
    private const val BASE_URL = "https://skvods.lol/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .method(original.method, original.body)
            chain.proceed(requestBuilder.build())
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: SkvodsApi = retrofit.create(SkvodsApi::class.java)
}
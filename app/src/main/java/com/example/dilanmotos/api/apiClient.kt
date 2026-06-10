package com.example.dilanmotos.api

import android.content.Context
import com.example.dilanmotos.session.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Inicializar con contexto para leer el token desde SessionManager
    private lateinit var sessionManager: SessionManager

    fun init(context: Context) {
        sessionManager = SessionManager(context)
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder().addInterceptor { chain ->
            val peticionOriginal = chain.request()
            val builder = peticionOriginal.newBuilder()

            // Inyectar token si existe en sesión
            val token = sessionManager.getToken()
            if (token.isNotEmpty()) {
                builder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(builder.build())
        }.build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
package com.example.dilanmotos.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val okHttpClient by lazy {
        OkHttpClient.Builder().addInterceptor { chain ->
            val peticionOriginal = chain.request()
            val builder = peticionOriginal.newBuilder()

            // 1. Intentamos obtener el contexto de la misma petición de Android de forma dinámica
            val context = chain.request().tag(Context::class.java)

            // 2. Si no viene en el tag, usamos un fallback seguro leyendo las SharedPreferences globales
            val token = context?.getSharedPreferences("DilanMotosPrefs", Context.MODE_PRIVATE)
                ?.getString("token_sesion", "")
                ?: ""

            // 3. Si el token no está vacío, se inyecta automáticamente en las cabeceras
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
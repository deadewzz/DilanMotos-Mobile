package com.example.dilanmotos.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    // VARIABLE GLOBAL TEMPORAL: Pon aquí un Token JWT válido de tu backend para probar.
    // (Más adelante lo ideal es guardarlo en SharedPreferences al hacer Login)
    var tokenUsuario: String? = null

    // 1. Creamos un cliente OkHttp que inyecta la seguridad en cada petición
    private val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val peticionOriginal = chain.request()
        val constructorPeticion = peticionOriginal.newBuilder()

        // Si tenemos un token guardado, se lo añadimos a la cabecera HTTP
        tokenUsuario?.let { token ->
            // NOTA: Revisa si tu Spring Boot lee "Bearer " o si solo pusiste "Authorization" a secas
            constructorPeticion.addHeader("Authorization", "Bearer $token")
        }

        chain.proceed(constructorPeticion.build())
    }.build()

    // 2. Modificamos el constructor de Retrofit para que use nuestro cliente seguro
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // <--- Agregamos esta línea clave aquí   
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
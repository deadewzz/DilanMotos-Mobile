package com.example.dilanmotos.api

import android.annotation.SuppressLint
import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("StaticFieldLeak")
object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"
    private var appContext: Context? = null

    /**
     * Registra el contexto global de la aplicación.
     * Lo ideal es llamarlo en una clase Application, pero para solucionarlo ya,
     * el interceptor lo buscará dinámicamente si no se ha asignado.
     */
    fun registrarContexto(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
        }
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder().addInterceptor { chain ->
            val peticionOriginal = chain.request()
            val builder = peticionOriginal.newBuilder()

            // Intentamos recuperar el contexto si no se ha registrado globalmente
            val context = appContext ?: chain.request().tag(Context::class.java)

            if (context != null) {
                // Forzamos la lectura en tiempo real del archivo XML directo del disco
                val prefs = context.getSharedPreferences("DilanMotosPrefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_sesion", "") ?: ""

                if (token.isNotEmpty()) {
                    builder.addHeader("Authorization", "Bearer $token")
                }
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
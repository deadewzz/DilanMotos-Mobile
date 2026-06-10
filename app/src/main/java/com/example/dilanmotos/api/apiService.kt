package com.example.dilanmotos.api

import com.example.dilanmotos.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // ==========================================
    // ENDPOINTS DE USUARIOS
    // ==========================================
    @POST("api/usuarios/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/usuarios")
    fun obtenerUsuarios(): Call<List<Usuario>>

    @POST("api/usuarios")
    fun crearUsuario(@Body usuario: Usuario): Call<Usuario>

    @PUT("api/usuarios/{id}")
    fun actualizarUsuario(@Path("id") id: Int, @Body usuario: Usuario): Call<Usuario>

    @DELETE("api/usuarios/{id}")
    fun eliminarUsuario(@Path("id") id: Int): Call<Void>

    // ==========================================
    // ENDPOINTS DE PRODUCTOS
    // ==========================================
    @GET("api/productos")
    fun obtenerProducto(): Call<List<Producto>>

    @POST("api/productos")
    fun crearProducto(@Body producto: Producto): Call<Producto>

    @PUT("api/productos/{id}")
    fun actualizarProducto(@Path("id") id: Int, @Body producto: Producto): Call<Producto>

    @DELETE("api/productos/{id}")
    fun eliminarProducto(@Path("id") id: Int): Call<Void>

    // ==========================================
    // ENDPOINTS DE MARCAS
    // ==========================================
    @GET("api/marcas")
    fun obtenerMarca(): Call<List<Marca>>

    @POST("api/marcas")
    fun crearMarca(@Body marca: Marca): Call<Marca>

    @PUT("api/marcas/{id}")
    fun actualizarMarca(@Path("id") id: Int, @Body marca: Marca): Call<Marca>

    @DELETE("api/marcas/{id}")
    fun eliminarMarca(@Path("id") id: Int): Call<Void>

    // ==========================================
    // ENDPOINTS DE MOTOS
    // ==========================================
    @GET("api/motos")
    fun obtenerMotos(): Call<List<Moto>>

    @POST("api/motos")
    fun crearMoto(@Body moto: Moto): Call<Moto>

    @PUT("api/motos/{id}")
    fun actualizarMoto(@Path("id") id: Int, @Body moto: Moto): Call<Moto>

    @DELETE("api/motos/{id}")
    fun eliminarMoto(@Path("id") id: Int): Call<Void>

    @POST("api/ia/consultar")
    fun consultarIA(@Body request: ConsultaRequest): Call<IaResponse>
}
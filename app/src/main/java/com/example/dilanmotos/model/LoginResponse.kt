package com.example.dilanmotos.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("rol")
    val rol: String,

    @SerializedName("id_usuario")
    val idUsuario: Int?
)
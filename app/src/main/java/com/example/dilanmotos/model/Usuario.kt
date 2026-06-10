package com.example.dilanmotos.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("idusuario")
    val idUsuario: Int?,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("contrasena")
    val contrasena: String?,

    @SerializedName("rol")
    val rol: String,

    @SerializedName("habilitado")
    val habilitado: Int = 1
)
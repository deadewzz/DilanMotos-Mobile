package com.example.dilanmotos.model

import com.google.gson.annotations.SerializedName

data class Moto(
    @SerializedName("idMoto")
    val idMoto: Int? = null,

    @SerializedName("idUsuario")
    val idUsuario: Int? = null,

    @SerializedName("idMarca")
    val idMarca: Int? = null,

    @SerializedName("modelo")
    val modelo: String? = null,

    @SerializedName("cilindraje")
    val cilindraje: Double? = null,
     // ✅ de vuelta

    @SerializedName("marca")
    val marca: Marca? = null
)
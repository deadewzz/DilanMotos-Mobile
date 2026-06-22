package com.example.dilanmotos.model

import com.google.gson.annotations.SerializedName

data class Referencia(
    @SerializedName("idReferencia")
    val idReferencia: Int? = null,

    @SerializedName("nombre")
    val nombre: String? = null,

    @SerializedName("idMarca")
    val idMarca: Int? = null,

    @SerializedName("cilindraje")
    val cilindraje: Double? = null
)
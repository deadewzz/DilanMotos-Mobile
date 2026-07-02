package com.example.dilanmotos.model

import com.google.gson.annotations.SerializedName

data class ConsultaRequest(
    @SerializedName("idUsuario")
    val idUsuario: Int,

    @SerializedName("motor")
    val motor: String,

    @SerializedName("falla")
    val falla: String
)
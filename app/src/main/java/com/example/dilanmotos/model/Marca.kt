package com.example.dilanmotos.model

import com.google.gson.annotations.SerializedName

data class Marca(
    @SerializedName("idMarca")
    val idMarca: Int? = null,

    @SerializedName("nombre")
    val nombre: String? = null,
)
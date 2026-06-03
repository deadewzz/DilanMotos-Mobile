package com.example.dilanmotos.model

import com.google.gson.annotations.SerializedName

data class Producto(

    @SerializedName("idProducto")
    val idProducto: Int? = null,

    @SerializedName("idCategoria")
    val idCategoria: Int,

    @SerializedName("idMarca")
    val idMarca: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("precio")
    val precio: Double,

    @SerializedName("imagenUrl")
    val imagenUrl: String?
)
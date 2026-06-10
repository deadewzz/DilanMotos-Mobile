package com.example.dilanmotos.model

import com.google.gson.annotations.SerializedName

data class Cotizacion(

    @SerializedName("idCotizacion")
    val idCotizacion: Int? = null,

    @SerializedName("idUsuario")
    val idUsuario: Int,

    @SerializedName("idProducto")
    val idProducto: Int,

    @SerializedName("producto")
    val producto: String,

    @SerializedName("cantidad")
    val cantidad: Int,

    @SerializedName("precioUnitario")
    val precioUnitario: Double,

    @SerializedName("fecha")
    val fecha: String,

    @SerializedName("producto_agregado")
    val productoAgregado: Boolean = true
)
package com.example.dilanmotos.model

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("token")
    val token: String,

    @SerializedName("nuevaContrasena")
    val nuevaContrasena: String
)
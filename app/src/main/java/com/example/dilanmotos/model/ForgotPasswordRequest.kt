package com.example.dilanmotos.model

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(
    @SerializedName("correo")
    val correo: String
)
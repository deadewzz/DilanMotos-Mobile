package com.example.dilanmotos.model

import com.google.gson.annotations.SerializedName

data class IaResponse(
    @SerializedName("content") val content: String
)
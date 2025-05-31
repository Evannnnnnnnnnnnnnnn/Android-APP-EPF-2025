package com.example.projetandroiddorspasteau.model

import com.google.gson.annotations.SerializedName

data class ApiCart(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("date")
    val date: String, // Format YYYY-MM-DD
    @SerializedName("products")
    val products: List<ApiCartProduct>
)
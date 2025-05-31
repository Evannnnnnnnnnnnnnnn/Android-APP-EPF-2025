package com.example.projetandroiddorspasteau.model

import com.google.gson.annotations.SerializedName

data class ApiCartProduct(
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("quantity")
    var quantity: Int
)
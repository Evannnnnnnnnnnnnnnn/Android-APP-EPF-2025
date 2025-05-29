package com.example.projetandroiddorspasteau.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("description")
    val description: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("image")
    val imageUrl: String,
    @SerializedName("rating")
    val rating: Rating
) : Parcelable

@Parcelize
data class Rating(
    @SerializedName("rate")
    val rate: Double,
    @SerializedName("count")
    val count: Int
) : Parcelable
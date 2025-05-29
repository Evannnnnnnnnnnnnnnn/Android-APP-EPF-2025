package com.example.projetandroiddorspasteau // Ton package


import com.example.projetandroiddorspasteau.model.Product
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("products") // Endpoint pour obtenir tous les produits
    suspend fun getAllProducts(): Response<List<Product>>

    @GET("products/categories")
    suspend fun getCategories(): Response<List<String>> // Pour tes catégories
}
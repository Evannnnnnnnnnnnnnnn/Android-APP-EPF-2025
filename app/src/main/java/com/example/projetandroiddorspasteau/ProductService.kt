package com.example.projetandroiddorspasteau

import com.example.projetandroiddorspasteau.model.Product
import retrofit2.http.GET
import retrofit2.http.Query


interface ProductService {
    @GET("products")
    suspend fun loadProduct(@Query("results") size : Int = 20) : GetProductResponse
}

data class GetProductResponse(val results: List<Product>)



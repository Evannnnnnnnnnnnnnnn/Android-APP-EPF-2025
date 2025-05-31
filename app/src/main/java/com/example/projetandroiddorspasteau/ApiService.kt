package com.example.projetandroiddorspasteau

import com.example.projetandroiddorspasteau.model.ApiCart
import com.example.projetandroiddorspasteau.model.Product
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Path

interface ApiService {
    @GET("products")
    suspend fun getAllProducts(): Response<List<Product>>

    @GET("products/categories")
    suspend fun getCategories(): Response<List<String>>

    @GET("carts/user/{userId}")
    suspend fun getUserCarts(@Path("userId") userId: Int): Response<List<ApiCart>>

    @POST("carts")
    suspend fun createCart(@Body cart: ApiCart): Response<ApiCart>

    @PUT("carts/{cartId}")
    suspend fun updateCart(@Path("cartId") cartId: Int, @Body cart: ApiCart): Response<ApiCart>

    @DELETE("carts/{cartId}")
    suspend fun deleteCart(@Path("cartId") cartId: Int): Response<Void>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: Int): Response<Product>
}
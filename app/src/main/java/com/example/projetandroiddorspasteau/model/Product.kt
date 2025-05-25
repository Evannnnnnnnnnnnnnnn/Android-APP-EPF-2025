package com.example.projetandroiddorspasteau.model

import android.os.Parcelable
import android.util.Log
import com.example.projetandroiddorspasteau.ProductService
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val TAG = "Product"

@Parcelize
data class Product(
    val title: String,
    val price: Float,
    val description: String,
    val category: String,
    val image: String,
    val rate: Float,
    val count: Int
): Parcelable{
    companion object Nb{
        suspend fun getProduct() :List<Product>{
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://fakestoreapi.com/products")
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client)
                .build()

            val productService = retrofit.create(ProductService::class.java)

            val getProductResponse = productService.loadProduct()
            Log.i(TAG, "getProduct : ${getProductResponse}")

            val products = getProductResponse.results.map {
                Product( //On appelle le concrusteur, mais pas de new en Kotlin
                    it.title,
                    it.price,
                    it.description,
                    it.category,
                    it.image,
                    it.rate,
                    it.count
                )
            }
            return products
        }
    }
}
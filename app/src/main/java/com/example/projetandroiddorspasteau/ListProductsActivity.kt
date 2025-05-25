package com.example.projetandroiddorspasteau

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.runBlocking
import com.example.projetandroiddorspasteau.model.Product

private const val TAG = "ListProductsActivity"

class ListProductsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_products)

        val recyclerView = findViewById<RecyclerView>(R.id.list_products_recyclerview)

        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        runBlocking {
            val products = Product.getProduct()
            recyclerView.adapter = ProductAdapter(products)
        }
    }
}
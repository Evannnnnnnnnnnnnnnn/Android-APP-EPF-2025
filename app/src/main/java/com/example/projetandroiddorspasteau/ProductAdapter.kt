package com.example.projetandroiddorspasteau

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projetandroiddorspasteau.model.Product

class ProductViewHolder (view : View): RecyclerView.ViewHolder(view)

const val PRODUCT_DATA = "product.data"

class ProductAdapter (val products: List<Product>) : RecyclerView.Adapter<ProductViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.product_view, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount()= products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val itemView = holder.itemView

//        itemView.click {
//            with(itemView.context){
//                val intent = Intent(this, DetailsProductActivity::class.java).apply {
//                    putExtra(PRODUCT_DATA, product)
//                }
//                startActivity(intent)
//            }
//
//        }

        itemView.findViewById<TextView>(R.id.product_title_textview).apply {
            text = product.title
        }

        itemView.findViewById<ImageView>(R.id.product_image_imageview).let {
            if(product.image.isBlank()) {
                Glide.with(itemView).load(product.image).into(it)//TODO avoir un placeholder
            }else{
                Glide.with(itemView).load(product.image).into(it)
            }

        }
    }
}
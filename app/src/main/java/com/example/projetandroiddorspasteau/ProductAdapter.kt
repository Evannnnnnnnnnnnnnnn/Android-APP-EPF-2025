package com.example.projetandroiddorspasteau

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.projetandroiddorspasteau.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onItemClicked: (Product) -> Unit // Callback pour le clic
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            onItemClicked(product)
        }
    }

    override fun getItemCount(): Int = products.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged() // Simple, mais pour de grandes listes, utiliser DiffUtil
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.product_image)
        private val productTitle: TextView = itemView.findViewById(R.id.product_title)
        private val productPrice: TextView = itemView.findViewById(R.id.product_price)

        fun bind(product: Product) {
            productTitle.text = product.title
            productPrice.text = String.format("€%.2f", product.price) // Formatage du prix

            var tolerance_appliqué = 30
            if (product.id == 1 || product.id == 9 || product.id == 10 || product.id == 11 || product.id == 12 || product.id == 15 || product.id == 16) {
                tolerance_appliqué = 150
            }
            if (product.id == 2 || product.id == 8 || product.id == 20) {
                tolerance_appliqué = 20
            }
            if (product.id == 4 || product.id == 17) {
                tolerance_appliqué = 100
            }
            if (product.id == 18) {
                tolerance_appliqué = 1
            }

            Glide.with(itemView.context)
                .load(product.imageUrl)
                .transform(WhiteToTransparentTransformation(tolerance = tolerance_appliqué))
                .placeholder(R.drawable.whysoserious) // Optionnel: une image de placeholder
                .error(R.drawable.whysoserious) // Optionnel: une image en cas d'erreur
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and transformed
                .into(productImage)
        }
    }
}
package com.example.projetandroiddorspasteau

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy // Import for cache strategy
import com.example.projetandroiddorspasteau.WhiteToTransparentTransformation

class CartAdapter(
    private var cartItems: List<CartItemDetail>,
    private val context: Context, // Pour CartManager
    private val onQuantityChanged: (productId: Int, newQuantity: Int) -> Unit,
    private val onItemRemoved: (productId: Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateCartItems(newCartItems: List<CartItemDetail>) {
        this.cartItems = newCartItems
        notifyDataSetChanged() // Pour une liste plus grande, utiliser DiffUtil
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImage: ImageView = itemView.findViewById(R.id.cart_item_image)
        private val itemTitle: TextView = itemView.findViewById(R.id.cart_item_title)
        private val itemPrice: TextView = itemView.findViewById(R.id.cart_item_price)
        private val itemQuantityText: TextView = itemView.findViewById(R.id.cart_item_quantity_text)
        private val decreaseQuantityButton: ImageButton = itemView.findViewById(R.id.cart_item_decrease_quantity)
        private val increaseQuantityButton: ImageButton = itemView.findViewById(R.id.cart_item_increase_quantity)
        private val removeItemButton: ImageButton = itemView.findViewById(R.id.cart_item_remove_button)

        fun bind(cartItem: CartItemDetail) {
            itemTitle.text = cartItem.product.title
            itemPrice.text = String.format("€%.2f", cartItem.product.price)
            itemQuantityText.text = cartItem.quantity.toString()

            var tolerance_appliqué = 30
            if (cartItem.product.id == 1 || cartItem.product.id == 9 || cartItem.product.id == 10 || cartItem.product.id == 11 || cartItem.product.id == 12 || cartItem.product.id == 15 || cartItem.product.id == 16) {
                tolerance_appliqué = 150
            }
            if (cartItem.product.id == 2 || cartItem.product.id == 8 || cartItem.product.id == 20) {
                tolerance_appliqué = 20
            }
            if (cartItem.product.id == 4 || cartItem.product.id == 17) {
                tolerance_appliqué = 100
            }
            if (cartItem.product.id == 18) {
                tolerance_appliqué = 1
            }

            Glide.with(itemView.context)
                .load(cartItem.product.imageUrl)
                .transform(WhiteToTransparentTransformation(tolerance = tolerance_appliqué))
                .placeholder(R.drawable.whysoserious)
                .error(R.drawable.whysoserious)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and transformed
                .into(itemImage)

            decreaseQuantityButton.setOnClickListener {
                if (cartItem.quantity > 1) {
                    onQuantityChanged(cartItem.product.id, cartItem.quantity - 1)
                } else {
                    // Si la quantité est 1 et on diminue, on supprime l'item
                    onItemRemoved(cartItem.product.id)
                }
            }

            increaseQuantityButton.setOnClickListener {
                // Optionnel: ajouter une limite max si besoin
                onQuantityChanged(cartItem.product.id, cartItem.quantity + 1)
            }

            removeItemButton.setOnClickListener {
                onItemRemoved(cartItem.product.id)
            }
        }
    }
}
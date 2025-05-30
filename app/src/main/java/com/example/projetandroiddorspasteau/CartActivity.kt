package com.example.projetandroiddorspasteau

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout // Importe LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {

    private lateinit var toolbarCart: Toolbar
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var emptyCartLayout: LinearLayout // Nouvelle référence au layout
    private lateinit var cartTotalPriceText: TextView
    private lateinit var checkoutButton: Button
    private lateinit var clearCartButton: Button

    private val cartUpdateListener: () -> Unit = {
        updateCartUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbarCart = findViewById(R.id.toolbar_cart)
        setSupportActionBar(toolbarCart)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mon Panier"

        cartRecyclerView = findViewById(R.id.cart_recycler_view)
        emptyCartLayout = findViewById(R.id.empty_cart_layout) // Récupère le LinearLayout
        cartTotalPriceText = findViewById(R.id.cart_total_price_text)
        checkoutButton = findViewById(R.id.checkout_button)
        clearCartButton = findViewById(R.id.clear_cart_button)

        setupRecyclerView()
        updateCartUI()

        CartManager.addCartUpdateListener(cartUpdateListener)

        checkoutButton.setOnClickListener {
            if (CartManager.getCartItems().isNotEmpty()) {
                Toast.makeText(this, "Fonctionnalité de validation non implémentée.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Votre panier est vide.", Toast.LENGTH_SHORT).show()
            }
        }

        clearCartButton.setOnClickListener {
            CartManager.clearCart(this)
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            CartManager.getCartItems(),
            this,
            onQuantityChanged = { productId, newQuantity ->
                CartManager.updateItemQuantity(productId, newQuantity, this)
            },
            onItemRemoved = { productId ->
                CartManager.removeItem(productId, this)
            }
        )
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter
    }

    private fun updateCartUI() {
        val items = CartManager.getCartItems()
        cartAdapter.updateCartItems(items)

        if (items.isEmpty()) {
            cartRecyclerView.visibility = View.GONE
            emptyCartLayout.visibility = View.VISIBLE // Affiche le layout du panier vide
            checkoutButton.isEnabled = false
        } else {
            cartRecyclerView.visibility = View.VISIBLE
            emptyCartLayout.visibility = View.GONE // Cache le layout du panier vide
            checkoutButton.isEnabled = true
        }
        cartTotalPriceText.text = String.format("€%.2f", CartManager.getTotalPrice())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        CartManager.removeCartUpdateListener(cartUpdateListener)
    }
}
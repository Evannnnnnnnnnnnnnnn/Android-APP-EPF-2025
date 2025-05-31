package com.example.projetandroiddorspasteau

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button // Peut être remplacé par MaterialButton si tu es spécifique
import com.google.android.material.button.MaterialButton // Mieux pour les MaterialButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {

    private lateinit var toolbarCart: Toolbar
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var emptyCartLayout: LinearLayout
    private lateinit var cartTotalPriceText: TextView
    // Références aux deux boutons de validation
    private lateinit var checkoutButtonPrimary: MaterialButton
    private lateinit var checkoutButtonOutlined: MaterialButton
    private lateinit var clearCartButton: MaterialButton // Garde MaterialButton pour la cohérence

    private val cartUpdateListener: () -> Unit = {
        updateCartUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbarCart = findViewById(R.id.toolbar_cart)
        setSupportActionBar(toolbarCart)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mon Panier" // Tu peux utiliser @string/cart_title

        cartRecyclerView = findViewById(R.id.cart_recycler_view)
        emptyCartLayout = findViewById(R.id.empty_cart_layout)
        cartTotalPriceText = findViewById(R.id.cart_total_price_text)
        checkoutButtonPrimary = findViewById(R.id.checkout_button_primary)
        checkoutButtonOutlined = findViewById(R.id.checkout_button_outlined)
        clearCartButton = findViewById(R.id.clear_cart_button)

        setupRecyclerView()
        updateCartUI() // Affichage initial

        CartManager.addCartUpdateListener(cartUpdateListener)

        // Le OnClickListener doit être sur le bouton qui est réellement cliquable
        // Ou tu peux avoir un seul listener et vérifier quel bouton est visible
        val checkoutClickListener = View.OnClickListener {
            if (CartManager.getCartItems().isNotEmpty()) {
                Toast.makeText(this, "Fonctionnalité de validation non implémentée.", Toast.LENGTH_LONG).show()
            } else {
                // Ce cas ne devrait pas se produire si le bouton primary est caché quand le panier est vide
                Toast.makeText(this, "Votre panier est vide.", Toast.LENGTH_SHORT).show()
            }
        }
        checkoutButtonPrimary.setOnClickListener(checkoutClickListener)
        // Si tu veux que le bouton outlined fasse quelque chose (ex: afficher un Toast "Panier vide")
        // checkoutButtonOutlined.setOnClickListener {
        //     Toast.makeText(this, "Votre panier est vide pour valider.", Toast.LENGTH_SHORT).show()
        // }


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
            emptyCartLayout.visibility = View.VISIBLE

            checkoutButtonPrimary.visibility = View.GONE
            checkoutButtonOutlined.visibility = View.VISIBLE
            checkoutButtonOutlined.isEnabled = false // Optionnel: garder désactivé
            clearCartButton.isEnabled = false // Désactiver "Vider le panier" si déjà vide

        } else {
            cartRecyclerView.visibility = View.VISIBLE
            emptyCartLayout.visibility = View.GONE

            checkoutButtonPrimary.visibility = View.VISIBLE
            checkoutButtonOutlined.visibility = View.GONE
            clearCartButton.isEnabled = true // Activer "Vider le panier"
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
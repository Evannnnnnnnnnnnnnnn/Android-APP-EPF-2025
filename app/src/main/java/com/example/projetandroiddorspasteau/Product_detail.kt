package com.example.projetandroiddorspasteau

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.projetandroiddorspasteau.model.Product
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.widget.NumberPicker
import com.bumptech.glide.load.engine.DiskCacheStrategy

class Product_detail : AppCompatActivity() {

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    private lateinit var toolbarDetail: Toolbar
    private lateinit var productDetailImage: ImageView
    private lateinit var productDetailTitle: TextView
    private lateinit var productDetailPrice: TextView
    private lateinit var productDetailCategory: TextView
    private lateinit var productDetailRatingBar: RatingBar
    private lateinit var productDetailRatingText: TextView
    private lateinit var productDetailDescription: TextView
    private lateinit var addToCartButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        toolbarDetail = findViewById(R.id.toolbar_detail)
        setSupportActionBar(toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // supportActionBar?.title = "" // On peut laisser ou mettre le titre par défaut si besoin

        productDetailImage = findViewById(R.id.product_detail_image)
        productDetailTitle = findViewById(R.id.product_detail_title)
        productDetailPrice = findViewById(R.id.product_detail_price)
        productDetailCategory = findViewById(R.id.product_detail_category)
        productDetailRatingBar = findViewById(R.id.product_detail_rating_bar)
        productDetailRatingText = findViewById(R.id.product_detail_rating_text)
        productDetailDescription = findViewById(R.id.product_detail_description)
        addToCartButton = findViewById(R.id.add_to_cart_button)

        val product =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_PRODUCT, Product::class.java) // Version pour API >= 33 -> tel Seb (le riche là)
            } else {
                // Suppress deprecation warning for the older getParcelableExtra method
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<Product>(EXTRA_PRODUCT) // Version pour API < 33
            }

        if (product != null) {
            displayProductDetails(product)
        } else {
            Toast.makeText(this, "Erreur: Produit non trouvé", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun displayProductDetails(product: Product) {
        // collapsingToolbarLayoutDetail.title = product.title // Supprimer cette ligne
        supportActionBar?.title = product.title // Le titre est maintenant sur la Toolbar standard

        Glide.with(this)
            .load(product.imageUrl)
            .transform(WhiteToTransparentTransformation(tolerance = 15))
            .placeholder(R.drawable.whysoserious)
            .error(R.drawable.whysoserious)
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and transformed
            .into(productDetailImage)

        productDetailTitle.text = product.title
        productDetailPrice.text = String.format("€%.2f", product.price)
        productDetailCategory.text = product.category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        productDetailRatingBar.rating = product.rating.rate.toFloat()
        productDetailRatingText.text = String.format("(%.1f - %d avis)", product.rating.rate, product.rating.count)
        productDetailDescription.text = product.description

        addToCartButton.setOnClickListener {
            showQuantityDialog(product)
        }
    }
    private fun showQuantityDialog(product: Product) {
        val numberPicker = NumberPicker(this).apply {
            minValue = 1
            maxValue = 10 // Limite max pour la quantité
            wrapSelectorWheel = false
            // Mettre la valeur actuelle si le produit est déjà dans le panier
            val existingItem = CartManager.getCartItems().find { it.product.id == product.id }
            value = existingItem?.quantity ?: 1
        }

        AlertDialog.Builder(this)
            .setTitle("Choisir la quantité")
            .setView(numberPicker)
            .setPositiveButton("Ajouter") { _, _ ->
                val quantity = numberPicker.value
                CartManager.addItem(product, quantity, this)
                showAddedToCartDialog(product.title)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }


    private fun showAddedToCartDialog(productName: String) {
        AlertDialog.Builder(this)
            .setTitle("Produit Ajouté !")
            .setMessage("$productName a été ajouté à votre panier.")
            .setPositiveButton("Voir le Panier") { _, _ ->
                // Lancer CartActivity
                val intent = Intent(this, CartActivity::class.java)
                // Optionnel : si tu veux que CartActivity se comporte d'une certaine manière
                // en venant d'ici, tu peux ajouter des extras.
                // Par exemple, pour éviter de relancer MainActivity si on fait "retour" depuis le panier :
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                // Optionnel: fermer Product_detail après être allé au panier
                // finish()
            }
            .setNegativeButton("Continuer les achats") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false) // Empêche de fermer le dialogue en cliquant à l'extérieur
            .show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
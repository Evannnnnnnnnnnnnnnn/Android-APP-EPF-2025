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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
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
        supportActionBar?.themedContext?.let { themedContext ->
            val upArrow = ContextCompat.getDrawable(themedContext, androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            upArrow?.let {
                DrawableCompat.setTint(it, ContextCompat.getColor(themedContext, R.color.app_toolbar_content_color))
                supportActionBar?.setHomeAsUpIndicator(it)
            }
        }
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
        // Gonfler le layout personnalisé pour le dialogue
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom_quantity, null)
        val numberPicker: NumberPicker = dialogView.findViewById(R.id.custom_dialog_number_picker)
        // val dialogTitleTextView: TextView = dialogView.findViewById(R.id.dialog_title_custom) // Si tu gères le titre via XML

        // Configuration du NumberPicker
        numberPicker.minValue = 0
        numberPicker.maxValue = 10
        numberPicker.wrapSelectorWheel = false
        val existingItem = CartManager.getCartItems().find { it.product.id == product.id }
        numberPicker.value = existingItem?.quantity ?: 1

        // Construire le dialogue
        val alertDialog = AlertDialog.Builder(this)
            // .setTitle("Choisir la quantité") // le titre est dans le XML
            .setView(dialogView)
            .setPositiveButton("Ajouter") { _, _ ->
                val quantity = numberPicker.value
                if (quantity > 0) {
                    CartManager.addItem(product, quantity, this)
                    showAddedToCartDialog(product.title)
                }
            }
            .setNegativeButton("Annuler", null)
            .create() // Crée le dialogue mais ne l'affiche pas encore


        alertDialog.show()

        val window = alertDialog.window
        val displayMetrics = resources.displayMetrics
        val dialogWidth = (displayMetrics.widthPixels * 0.7).toInt() // 70% de la largeur de l'écran
        window?.setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER) // Assurer qu'il soit centré
    }


    private fun showAddedToCartDialog(productName: String) {
        AlertDialog.Builder(this)
            .setTitle("Produit Ajouté !")
            .setMessage("$productName a été ajouté à votre panier.")
            .setPositiveButton("Voir le Panier") { _, _ ->
                // Lancer CartActivity
                val intent = Intent(this, CartActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                // fermer Product_detail après être allé au panier
                finish()
            }
            .setNegativeButton("Continuer les achats") { dialog, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
            .setCancelable(true) // Empêche de fermer le dialogue en cliquant à l'extérieur à false
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
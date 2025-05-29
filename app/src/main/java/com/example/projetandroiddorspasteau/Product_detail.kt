package com.example.projetandroiddorspasteau

import android.annotation.SuppressLint
import android.os.Build
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

// import com.google.android.material.appbar.CollapsingToolbarLayout // Supprimer cet import si présent

class Product_detail : AppCompatActivity() {

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    private lateinit var toolbarDetail: Toolbar
    // private lateinit var collapsingToolbarLayoutDetail: CollapsingToolbarLayout // Supprimer cette variable
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
        // collapsingToolbarLayoutDetail = findViewById(R.id.collapsing_toolbar_layout_detail) // Supprimer cette ligne
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
            intent.getParcelableExtra(EXTRA_PRODUCT, Product::class.java)

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
            .placeholder(R.drawable.whysoserious)
            .error(R.drawable.whysoserious)
            .into(productDetailImage)

        productDetailTitle.text = product.title
        productDetailPrice.text = String.format("€%.2f", product.price)
        productDetailCategory.text = product.category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        productDetailRatingBar.rating = product.rating.rate.toFloat()
        productDetailRatingText.text = String.format("(%.1f - %d avis)", product.rating.rate, product.rating.count)
        productDetailDescription.text = product.description

        addToCartButton.setOnClickListener {
            Toast.makeText(this, "${product.title} ajouté au panier (simulation)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
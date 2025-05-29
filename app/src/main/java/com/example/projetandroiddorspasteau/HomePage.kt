package com.example.projetandroiddorspasteau // Ton package

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetandroiddorspasteau.model.Product
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    // private lateinit var welcomeText: TextView // On ne les utilise plus directement pour l'instant
    // private lateinit var selectedCategoryText: TextView
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var progressBar: ProgressBar

    private var allProducts: List<Product> = emptyList() // Garder une copie de tous les produits
    private var currentCategories: List<String> = emptyList()
    private var currentSelectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // S'assurer que le thème NoActionBar est appliqué si tu l'as fait dans le Manifest
        // (Normalement, c'est déjà géré par le Manifest)
        setContentView(R.layout.activity_home_page)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "PasteauDors"

        // welcomeText = findViewById(R.id.welcome_text)
        // selectedCategoryText = findViewById(R.id.selected_category_text)
        productsRecyclerView = findViewById(R.id.products_recycler_view)
        progressBar = findViewById(R.id.progress_bar)

        setupRecyclerView()
        fetchProducts()
        fetchCategories() // Pour le menu déroulant
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList()) { product ->
            // Gérer le clic sur un produit
            val intent = Intent(this, Product_detail::class.java)
            intent.putExtra(Product_detail.EXTRA_PRODUCT, product) // Passe l'objet Product
            startActivity(intent)
        }
        productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productsRecyclerView.adapter = productAdapter
    }

    private fun fetchProducts(category: String? = null) {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getAllProducts()
                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        allProducts = products
                        filterAndDisplayProducts(category)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Erreur de chargement des produits", Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "Error fetching products: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "Exception fetching products", e)
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun filterAndDisplayProducts(category: String?) {
        currentSelectedCategory = category
        val productsToDisplay = if (category == null) {
            allProducts
        } else {
            allProducts.filter { it.category.equals(category, ignoreCase = true) }
        }
        productAdapter.updateProducts(productsToDisplay)
        // updateSelectedCategoryUI() // Si tu veux toujours afficher le texte de la catégorie
    }


    private fun fetchCategories() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getCategories()
                if (response.isSuccessful) {
                    response.body()?.let { categories ->
                        currentCategories = categories.map { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } } // Capitalize
                    }
                } else {
                    Log.e("MainActivity", "Error fetching categories: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception fetching categories", e)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Rechercher un article..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    performSearch(query)
                } else {
                    filterAndDisplayProducts(currentSelectedCategory) // Affiche les produits filtrés ou tous si pas de catégorie
                }
                searchView.clearFocus()
                searchItem.collapseActionView()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    // Optionnel: réinitialiser la liste si la recherche est effacée
                    filterAndDisplayProducts(currentSelectedCategory)
                }
                return true
            }
        })
        return true
    }

    private fun performSearch(query: String) {
        val filteredProducts = allProducts.filter { product ->
            product.title.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true)
        }
        // Si une catégorie est sélectionnée, on peut vouloir chercher uniquement dans cette catégorie
        val productsToDisplay = if (currentSelectedCategory != null) {
            filteredProducts.filter { it.category.equals(currentSelectedCategory, ignoreCase = true) }
        } else {
            filteredProducts
        }
        productAdapter.updateProducts(productsToDisplay)
        if (productsToDisplay.isEmpty()) {
            Toast.makeText(this, "Aucun produit trouvé pour '$query'", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                Toast.makeText(this, "Panier cliqué", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_qrcode -> {
                Toast.makeText(this, "QRCode cliqué", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_filter_category -> {
                val anchorView = findViewById<View>(R.id.action_filter_category) ?: toolbar
                showCategoryPopupMenu(anchorView)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCategoryPopupMenu(anchor: View) {
        val popupMenu = PopupMenu(this, anchor)
        // Ajouter une option "Toutes les catégories"
        popupMenu.menu.add(Menu.NONE, -1, Menu.NONE, "Toutes les catégories") // ID spécial -1
        currentCategories.forEachIndexed { index, category ->
            popupMenu.menu.add(Menu.NONE, index, index, category)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == -1) { // "Toutes les catégories" sélectionné
                filterAndDisplayProducts(null)
                Toast.makeText(this, "Affichage de toutes les catégories", Toast.LENGTH_SHORT).show()
            } else {
                val selectedCat = currentCategories[menuItem.itemId]
                filterAndDisplayProducts(selectedCat)
                Toast.makeText(this, "Catégorie: $selectedCat", Toast.LENGTH_SHORT).show()
            }
            true
        }
        popupMenu.show()
    }

    // private fun updateSelectedCategoryUI() { // Si tu veux toujours l'afficher
    //     if (currentSelectedCategory != null) {
    //         selectedCategoryText.text = "Catégorie sélectionnée : $currentSelectedCategory"
    //         selectedCategoryText.visibility = View.VISIBLE
    //     } else {
    //         selectedCategoryText.text = "Toutes les catégories" // Ou cacher
    //         selectedCategoryText.visibility = View.VISIBLE // Ou GONE
    //     }
    // }
}
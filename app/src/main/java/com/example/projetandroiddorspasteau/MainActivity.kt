package com.example.projetandroiddorspasteau

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetandroiddorspasteau.model.Product
import kotlinx.coroutines.launch
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var progressBar: ProgressBar

    private var allProducts: List<Product> = emptyList()
    private var currentCategories: List<String> = emptyList()
    private var currentSelectedCategory: String? = null

    // Launchers pour le scan QRCode et la permission caméra
    private lateinit var scanActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var cartFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        productsRecyclerView = findViewById(R.id.products_recycler_view)
        progressBar = findViewById(R.id.progress_bar)

        initializeActivityLaunchers()
        cartFab = findViewById(R.id.fab_cart)

        cartFab.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        lifecycleScope.launch {
            Log.d("MainActivity", "onCreate: Launching coroutine to load cart and fetch data.")
            CartManager.loadCartFromDataStore(applicationContext)

            setupRecyclerView()
            fetchProducts()
            fetchCategories()
            CartManager.fetchCartFromServer(applicationContext)
            Log.d("MainActivity", "onCreate: Coroutine finished loading cart and fetching initial data.")
        }
    }

    private fun initializeActivityLaunchers() {
        scanActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val scannedIdString = data?.getStringExtra(ScanActivity.SCANNED_PRODUCT_ID)
                if (!scannedIdString.isNullOrBlank()) {
                    try {
                        val productId = scannedIdString.toInt()
                        Log.d("MainActivity", "Product ID from QR Code: $productId")
                        fetchAndShowProductById(productId)
                    } catch (e: NumberFormatException) {
                        Log.e("MainActivity", "Invalid product ID from QR Code: $scannedIdString", e)
                        Toast.makeText(this, "Format d'ID de produit invalide", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.d("MainActivity", "No product ID returned from scan.")
                }
            } else {
                Log.d("MainActivity", "Scan cancelled or failed.")
                // Toast.makeText(this, "Scan annulé", Toast.LENGTH_SHORT).show()
            }
        }

        requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Camera permission granted.")
                launchScanner()
            } else {
                Log.w("MainActivity", "Camera permission denied.")
                Toast.makeText(this, "Permission caméra requise pour scanner les QRCodes", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList()) { product ->
            val intent = Intent(this, Product_detail::class.java)
            intent.putExtra(Product_detail.EXTRA_PRODUCT, product)
            startActivity(intent)
        }
        productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productsRecyclerView.adapter = productAdapter
    }

    private fun fetchProducts(categoryToFilter: String? = null) {
        progressBar.visibility = View.VISIBLE
        Log.d("MainActivity", "fetchProducts called with category: $categoryToFilter")
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getAllProducts()
                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        allProducts = products
                        filterAndDisplayProducts(categoryToFilter ?: currentSelectedCategory)
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Erreur de chargement des produits",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(
                        "MainActivity",
                        "Error fetching products: ${response.code()} - ${response.message()}"
                    )
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
        Log.d("MainActivity", "Filtering for category: $category")

        val productsToDisplay = if (category == null) {
            allProducts
        } else {
            allProducts.filter { it.category.equals(category, ignoreCase = true) }
        }
        productAdapter.updateProducts(productsToDisplay)
    }


    private fun fetchCategories() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getCategories()
                if (response.isSuccessful) {
                    response.body()?.let { categories ->
                        currentCategories =
                            categories.map { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }
                    }
                } else {
                    Log.e(
                        "MainActivity",
                        "Error fetching categories: ${response.code()} - ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception fetching categories", e)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView =
            searchItem.actionView as SearchView

        searchView.queryHint = "Rechercher un article..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    performSearch(query)
                } else {
                    filterAndDisplayProducts(currentSelectedCategory)
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    filterAndDisplayProducts(currentSelectedCategory)
                } else if (newText.length > 1) {
                    performSearch(newText)
                }
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                filterAndDisplayProducts(currentSelectedCategory)
                return true
            }
        })
        return true
    }

    private fun performSearch(query: String) {
        Log.d(
            "MainActivity",
            "Performing search for: $query, current category: $currentSelectedCategory"
        )

        val searchedProducts = allProducts.filter { product ->
            product.title.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true)
        }

        val productsToDisplay = if (currentSelectedCategory != null) {
            searchedProducts.filter { product ->
                product.category.equals(currentSelectedCategory, ignoreCase = true)
            }
        } else {
            searchedProducts
        }

        productAdapter.updateProducts(productsToDisplay)

//        if (productsToDisplay.isEmpty()) {
//            Toast.makeText(this, "Aucun produit trouvé pour '$query'", Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_qrcode -> {
                // Demander la permission caméra avant de lancer le scanner
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
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
        popupMenu.menu.add(Menu.NONE, -1, Menu.NONE, "Toutes les catégories")
        currentCategories.forEachIndexed { index, category ->
            popupMenu.menu.add(Menu.NONE, index, index, category)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val searchMenuItem = toolbar.menu.findItem(R.id.action_search)
            if (searchMenuItem != null && searchMenuItem.isActionViewExpanded) {
                (searchMenuItem.actionView as SearchView).setQuery("", false)
                searchMenuItem.collapseActionView()
            }

            if (menuItem.itemId == -1) {
                filterAndDisplayProducts(null)
                //Toast.makeText(this, "Affichage de toutes les catégories", Toast.LENGTH_SHORT).show()
            } else {
                val selectedCat = currentCategories[menuItem.itemId]
                filterAndDisplayProducts(selectedCat)
                //Toast.makeText(this, "Catégorie: $selectedCat", Toast.LENGTH_SHORT).show()
            }
            true
        }
        popupMenu.show()
    }

    private fun launchScanner() {
        val intent = Intent(this, ScanActivity::class.java)
        scanActivityResultLauncher.launch(intent)
    }

    private fun fetchAndShowProductById(productId: Int) {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "Fetching product details for ID: $productId")
                val response = RetrofitInstance.api.getProductById(productId)
                if (response.isSuccessful) {
                    response.body()?.let { product ->
                        val intent = Intent(this@MainActivity, Product_detail::class.java)
                        intent.putExtra(Product_detail.EXTRA_PRODUCT, product)
                        startActivity(intent)
                    } ?: run {
                        Log.w("MainActivity", "Product with ID $productId not found or empty response body.")
                        Toast.makeText(this@MainActivity, "Produit non trouvé (ID: $productId)", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("MainActivity", "Error fetching product by ID $productId: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@MainActivity, "Erreur de chargement du produit (ID: $productId)", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception fetching product by ID $productId", e)
                Toast.makeText(this@MainActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
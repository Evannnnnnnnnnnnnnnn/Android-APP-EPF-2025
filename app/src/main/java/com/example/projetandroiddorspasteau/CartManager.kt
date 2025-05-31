package com.example.projetandroiddorspasteau

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.projetandroiddorspasteau.model.ApiCart
import com.example.projetandroiddorspasteau.model.ApiCartProduct
import com.example.projetandroiddorspasteau.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale


 data class CartItemDetail(
    val product: Product,
    var quantity: Int
 )

// Extension pour accéder au DataStore (à placer en dehors de l'objet CartManager ou dans un fichier séparé)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cart_preferences")

object CartManager {
    private const val TAG = "CartManager"
    private const val USER_ID = 1
    private var serverCartId: Int? = null

    private val localCartItems = mutableMapOf<Int, CartItemDetail>()

    private val cartUpdateListeners = mutableListOf<() -> Unit>()

    // Clé pour DataStore
    private val CART_ITEMS_KEY = stringPreferencesKey("cart_items_json")
    private val gson = Gson()

    fun addCartUpdateListener(listener: () -> Unit) {
        cartUpdateListeners.add(listener)
    }
    fun removeCartUpdateListener(listener: () -> Unit) {
        cartUpdateListeners.remove(listener)
    }
    private fun notifyCartUpdated() {
        CoroutineScope(Dispatchers.Main).launch {
            cartUpdateListeners.forEach { it.invoke() }
        }
    }


    fun getCartItems(): List<CartItemDetail> {
        return localCartItems.values.toList().sortedBy { it.product.title }
    }
    fun getTotalPrice(): Double {
        return localCartItems.values.sumOf { it.product.price * it.quantity }
    }
    fun getItemCount(): Int {
        return localCartItems.values.sumOf { it.quantity }
    }

    fun addItem(product: Product, quantity: Int, context: Context) {
        if (quantity <= 0) return

        val existingItem = localCartItems[product.id]
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            localCartItems[product.id] = CartItemDetail(product, quantity)
        }
        Log.d(TAG, "Added to local cart: ${product.title}, qty: $quantity. New total: ${localCartItems[product.id]?.quantity}")
        saveCartToDataStore(context)
        notifyCartUpdated()
        syncCartWithServer(context, "Produit ajouté au panier !")
    }

    fun updateItemQuantity(productId: Int, newQuantity: Int, context: Context) {
        if (newQuantity <= 0) {
            removeItem(productId, context)
            return
        }
        localCartItems[productId]?.let {
            it.quantity = newQuantity
            Log.d(TAG, "Updated quantity for ${it.product.title} to $newQuantity")
            saveCartToDataStore(context)
            notifyCartUpdated()
            syncCartWithServer(context, "Quantité mise à jour.")
        }
    }

    fun removeItem(productId: Int, context: Context) {
        val removedItem = localCartItems.remove(productId)
        if (removedItem != null) {
            Log.d(TAG, "Removed from local cart: ${removedItem.product.title}")
            saveCartToDataStore(context)
            notifyCartUpdated()
            syncCartWithServer(context, "${removedItem.product.title} retiré du panier.")
        }
    }

    fun clearCart(context: Context) {
        localCartItems.clear()
        Log.d(TAG, "Local cart cleared.")
        saveCartToDataStore(context)
        notifyCartUpdated()
        if (serverCartId != null) {
            deleteCartFromServer(context, "Panier vidé.")
        } else {
            //Toast.makeText(context, "Panier vidé.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun saveCartToDataStore(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cartList = localCartItems.values.toList()
                val jsonCart = gson.toJson(cartList)
                context.dataStore.edit { preferences ->
                    preferences[CART_ITEMS_KEY] = jsonCart
                }
                Log.d(TAG, "Cart saved to DataStore.")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving cart to DataStore", e)
            }
        }
    }

    suspend fun loadCartFromDataStore(context: Context) {
        try {
            val preferences = context.dataStore.data.first()
            val jsonCart = preferences[CART_ITEMS_KEY]
            if (!jsonCart.isNullOrEmpty()) {
                val type = object : TypeToken<List<CartItemDetail>>() {}.type
                val loadedItems: List<CartItemDetail> = gson.fromJson(jsonCart, type)
                localCartItems.clear()
                loadedItems.forEach { localCartItems[it.product.id] = it }
                Log.d(TAG, "Cart loaded from DataStore. Items: ${localCartItems.size}")
            } else {
                Log.d(TAG, "No cart data in DataStore.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading cart from DataStore", e)
        }
        notifyCartUpdated()
    }

    fun fetchCartFromServer(context: Context, onFinished: ((Boolean) -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Fetching cart for user $USER_ID")
                val response = RetrofitInstance.api.getUserCarts(USER_ID)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val carts = response.body()
                        if (!carts.isNullOrEmpty()) {
                            val serverCart = carts.first()
                            serverCartId = serverCart.id
                            Log.d(TAG, "Fetched cart ID: $serverCartId from server.")
                            //Toast.makeText(context, "Panier serveur (ID: $serverCartId) info récupérée.", Toast.LENGTH_SHORT).show()
                        } else {
                            serverCartId = null
                            Log.d(TAG, "No cart found on server for user $USER_ID.")
                        }
                        onFinished?.invoke(true)
                    } else {
                        Log.e(TAG, "Error fetching cart: ${response.code()} - ${response.message()}")
                        // Toast.makeText(context, "Erreur de chargement du panier serveur.", Toast.LENGTH_SHORT).show()
                        onFinished?.invoke(false)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching cart", e)
                withContext(Dispatchers.Main) {
                    // Toast.makeText(context, "Erreur réseau (panier): ${e.message}", Toast.LENGTH_SHORT).show()
                    onFinished?.invoke(false)
                }
            }
        }
    }


    private fun syncCartWithServer(context: Context, successMessage: String) {
        if (localCartItems.isEmpty()) {
            if (serverCartId != null) {
                deleteCartFromServer(context, successMessage.ifBlank { "Panier vidé sur le serveur." })
            }
//            else {
//                if (successMessage.isNotBlank()) //Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
//                }
            return
        }

        val apiProducts = localCartItems.map { ApiCartProduct(it.key, it.value.quantity) }
        val cartToSync = ApiCart(userId = USER_ID, date = getFormattedDate(), products = apiProducts)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (serverCartId == null) {
                    Log.d(TAG, "Creating new cart on server...")
                    RetrofitInstance.api.createCart(cartToSync)
                } else {
                    Log.d(TAG, "Updating cart ID $serverCartId on server...")
                    RetrofitInstance.api.updateCart(serverCartId!!, cartToSync)
                }

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val syncedCart = response.body()
                        if (syncedCart?.id != null) {
                            serverCartId = syncedCart.id
                        }
                        Log.d(TAG, "Cart synced with server. New/Updated Cart ID: $serverCartId")
//                        if (successMessage.isNotBlank()) //Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                        }
                    else {
                        Log.e(TAG, "Error syncing cart: ${response.code()} - ${response.message()}")
                        Toast.makeText(context, "Erreur de synchronisation du panier.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception syncing cart", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur réseau (synchro panier): ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteCartFromServer(context: Context, successMessage: String) {// Pour Main
    }
}
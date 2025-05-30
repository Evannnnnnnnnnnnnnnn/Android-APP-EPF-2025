package com.example.projetandroiddorspasteau

import android.content.Context
import android.icu.text.SimpleDateFormat // Pour la date
import android.util.Log
import android.widget.Toast
import com.example.projetandroiddorspasteau.model.ApiCart
import com.example.projetandroiddorspasteau.model.ApiCartProduct
import com.example.projetandroiddorspasteau.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale

// Data class pour gérer localement les items du panier avec les détails du produit
data class CartItemDetail(
    val product: Product,
    var quantity: Int
)

object CartManager {
    private const val TAG = "CartManager"
    private const val USER_ID = 1 // User ID fixe pour la démo
    private var serverCartId: Int? = null // ID du panier sur le serveur

    private val localCartItems = mutableMapOf<Int, CartItemDetail>() // productId -> CartItemDetail

    // Pour notifier les observateurs (ex: CartActivity) des changements
    private val cartUpdateListeners = mutableListOf<() -> Unit>()

    fun addCartUpdateListener(listener: () -> Unit) {
        cartUpdateListeners.add(listener)
    }

    fun removeCartUpdateListener(listener: () -> Unit) {
        cartUpdateListeners.remove(listener)
    }

    private fun notifyCartUpdated() {
        cartUpdateListeners.forEach { it.invoke() }
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
        notifyCartUpdated()
        syncCartWithServer(context, "Produit ajouté au panier !")
    }

    fun updateItemQuantity(productId: Int, newQuantity: Int, context: Context) {
        if (newQuantity <= 0) {
            removeItem(productId, context) // Si la quantité est 0 ou moins, supprimer l'item
            return
        }
        localCartItems[productId]?.let {
            it.quantity = newQuantity
            Log.d(TAG, "Updated quantity for ${it.product.title} to $newQuantity")
            notifyCartUpdated()
            syncCartWithServer(context, "Quantité mise à jour.")
        }
    }

    fun removeItem(productId: Int, context: Context) {
        val removedItem = localCartItems.remove(productId)
        if (removedItem != null) {
            Log.d(TAG, "Removed from local cart: ${removedItem.product.title}")
            notifyCartUpdated()
            syncCartWithServer(context, "${removedItem.product.title} retiré du panier.")
        }
    }

    fun clearCart(context: Context) {
        localCartItems.clear()
        Log.d(TAG, "Local cart cleared.")
        notifyCartUpdated()
        if (serverCartId != null) {
            // Si on vide le panier local et qu'un panier serveur existe, on le supprime
            deleteCartFromServer(context, "Panier vidé.")
        } else {
            Toast.makeText(context, "Panier vidé.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // --- API Synchronization ---

    fun fetchCartFromServer(context: Context, onFinished: ((Boolean) -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Fetching cart for user $USER_ID")
                val response = RetrofitInstance.api.getUserCarts(USER_ID)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val carts = response.body()
                        if (!carts.isNullOrEmpty()) {
                            val serverCart = carts.first() // On prend le premier panier de l'utilisateur
                            serverCartId = serverCart.id
                            Log.d(TAG, "Fetched cart ID: $serverCartId from server.")
                            // Pour une synchro complète, il faudrait récupérer les détails des produits
                            // et fusionner avec localCartItems. Pour l'instant, on se base sur ce qu'on ajoute.
                            // Si l'objectif est juste de récupérer un panier existant au lancement,
                            // il faudrait une logique pour peupler localCartItems à partir de serverCart.products
                            // et ensuite appeler fetchProductDetails pour chaque productId.
                            // Pour simplifier, on suppose que le local est la source de vérité pour le moment
                            // et que fetch est surtout pour obtenir serverCartId.
                            Toast.makeText(context, "Panier serveur récupéré (ID: $serverCartId)", Toast.LENGTH_SHORT).show()
                        } else {
                            serverCartId = null // Pas de panier pour cet utilisateur
                            Log.d(TAG, "No cart found on server for user $USER_ID.")
                        }
                        onFinished?.invoke(true)
                    } else {
                        Log.e(TAG, "Error fetching cart: ${response.code()} - ${response.message()}")
                        Toast.makeText(context, "Erreur de chargement du panier serveur.", Toast.LENGTH_SHORT).show()
                        onFinished?.invoke(false)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching cart", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur réseau: ${e.message}", Toast.LENGTH_SHORT).show()
                    onFinished?.invoke(false)
                }
            }
        }
    }


    private fun syncCartWithServer(context: Context, successMessage: String) {
        if (localCartItems.isEmpty()) {
            if (serverCartId != null) {
                // Si le panier local est vide mais qu'un panier serveur existe, le supprimer
                deleteCartFromServer(context, successMessage.ifBlank { "Panier vidé sur le serveur." })
            } else {
                // Panier local vide, pas de panier serveur, rien à faire
                if (successMessage.isNotBlank()) Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            }
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
                            serverCartId = syncedCart.id // Mettre à jour avec l'ID du panier (surtout si création)
                        }
                        Log.d(TAG, "Cart synced with server. New/Updated Cart ID: $serverCartId")
                        if (successMessage.isNotBlank()) Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "Error syncing cart: ${response.code()} - ${response.message()}")
                        Toast.makeText(context, "Erreur de synchronisation du panier.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception syncing cart", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur réseau: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteCartFromServer(context: Context, successMessage: String) {
        val cartIdToDelete = serverCartId ?: return // Pas d'ID serveur, rien à supprimer

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Deleting cart ID $cartIdToDelete from server...")
                val response = RetrofitInstance.api.deleteCart(cartIdToDelete)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Cart ID $cartIdToDelete deleted from server.")
                        serverCartId = null // Réinitialiser l'ID du panier serveur
                        if (successMessage.isNotBlank()) Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "Error deleting cart: ${response.code()} - ${response.message()}")
                        Toast.makeText(context, "Erreur de suppression du panier serveur.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception deleting cart", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur réseau: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
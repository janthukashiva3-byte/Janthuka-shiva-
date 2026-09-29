package com.example.data.repository

import com.example.data.SeedData
import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OmniRepository(private val db: AppDatabase) {

    val allStores: Flow<List<StoreEntity>> = db.storeDao().getAllStores()
    val allProducts: Flow<List<ProductEntity>> = db.productDao().getAllProducts()
    val cartItems: Flow<List<CartItemEntity>> = db.cartDao().getAllCartItems()
    val allOrders: Flow<List<OrderEntity>> = db.orderDao().getAllOrders()
    val activeOrders: Flow<List<OrderEntity>> = db.orderDao().getActiveOrders()
    val addresses: Flow<List<AddressEntity>> = db.addressDao().getAllAddresses()
    val wishlistIds: Flow<List<String>> = db.wishlistDao().getAllWishlistProductIds()

    init {
        // Pre-populate if empty
        CoroutineScope(Dispatchers.IO).launch {
            val existingStores = db.storeDao().getAllStores().first()
            if (existingStores.isEmpty()) {
                db.storeDao().insertStores(SeedData.STORES)
                db.productDao().insertProducts(SeedData.PRODUCTS)
                db.addressDao().insertAddresses(SeedData.ADDRESSES)
                db.reviewDao().insertReviews(SeedData.INITIAL_REVIEWS)

                // Also seed one sample delivered order for immediate order history & re-order demonstration
                val initialOrder = OrderEntity(
                    orderId = "OD-98214",
                    storeId = "store_apex_tech",
                    storeName = "Apex Tech & Gadgets",
                    itemsSummary = "1x 65W GaN Dual-Port Fast Charger, 1x Braided USB-C Cable (2m)",
                    totalAmount = 40.48,
                    status = "DELIVERED",
                    placedAtTimestamp = System.currentTimeMillis() - 86400000,
                    deliveryAddress = "742 Evergreen Terrace, Apt 4B",
                    paymentMethod = "OmniDrop Pay / Card",
                    deliveryPartnerName = "Marcus Vance",
                    deliveryPartnerPhone = "+1 (555) 234-8921",
                    deliveryPartnerVehicle = "Electric Scooter #ET-482",
                    deliveryOtp = "4821",
                    estimatedMinutes = 0
                )
                db.orderDao().insertOrder(initialOrder)
            }
        }
    }

    fun getStoreById(storeId: String): Flow<StoreEntity?> = db.storeDao().getStoreById(storeId)

    fun getProductsByStore(storeId: String): Flow<List<ProductEntity>> =
        db.productDao().getProductsByStore(storeId)

    fun getProductById(productId: String): Flow<ProductEntity?> =
        db.productDao().getProductById(productId)

    fun getReviews(targetType: String, targetId: String): Flow<List<ReviewEntity>> =
        db.reviewDao().getReviews(targetType, targetId)

    fun searchProducts(query: String): Flow<List<ProductEntity>> =
        db.productDao().searchProducts(query)

    fun getOrderById(orderId: String): Flow<OrderEntity?> = db.orderDao().getOrderById(orderId)

    fun isProductInWishlist(productId: String): Flow<Boolean> =
        db.wishlistDao().isInWishlist(productId)

    suspend fun toggleWishlist(productId: String) = withContext(Dispatchers.IO) {
        val inList = db.wishlistDao().isInWishlist(productId).first()
        if (inList) {
            db.wishlistDao().removeFromWishlist(productId)
        } else {
            db.wishlistDao().addToWishlist(WishlistEntity(productId))
        }
    }

    suspend fun addToCart(productId: String, storeId: String, quantity: Int = 1) = withContext(Dispatchers.IO) {
        val existing = db.cartDao().getCartItem(productId)
        if (existing != null) {
            val newQty = existing.quantity + quantity
            if (newQty > 0) {
                db.cartDao().updateCartItem(existing.copy(quantity = newQty))
            } else {
                db.cartDao().deleteCartItem(productId)
            }
        } else if (quantity > 0) {
            db.cartDao().insertCartItem(CartItemEntity(productId, storeId, quantity))
        }
    }

    suspend fun updateCartQuantity(productId: String, quantity: Int) = withContext(Dispatchers.IO) {
        if (quantity <= 0) {
            db.cartDao().deleteCartItem(productId)
        } else {
            val existing = db.cartDao().getCartItem(productId)
            if (existing != null) {
                db.cartDao().updateCartItem(existing.copy(quantity = quantity))
            }
        }
    }

    suspend fun clearCart() = withContext(Dispatchers.IO) {
        db.cartDao().clearCart()
    }

    suspend fun placeOrder(
        items: List<Pair<ProductEntity, Int>>,
        store: StoreEntity,
        deliveryAddress: String,
        paymentMethod: String,
        totalAmount: Double
    ): String = withContext(Dispatchers.IO) {
        val orderNum = (10000..99999).random()
        val orderId = "OD-$orderNum"
        val otp = (1000..9999).random().toString()
        val summary = items.joinToString(", ") { "${it.second}x ${it.first.name}" }

        val order = OrderEntity(
            orderId = orderId,
            storeId = store.id,
            storeName = store.name,
            itemsSummary = summary,
            totalAmount = totalAmount,
            status = "PLACED",
            placedAtTimestamp = System.currentTimeMillis(),
            deliveryAddress = deliveryAddress,
            paymentMethod = paymentMethod,
            deliveryPartnerName = "Marcus Vance",
            deliveryPartnerPhone = "+1 (555) 789-0192",
            deliveryPartnerVehicle = "Eco Scooter #ET-592",
            deliveryOtp = otp,
            estimatedMinutes = store.deliveryTimeMinutes
        )

        db.orderDao().insertOrder(order)
        db.cartDao().clearCart()
        orderId
    }

    suspend fun advanceOrderStatus(orderId: String, currentStatus: String): String = withContext(Dispatchers.IO) {
        val nextStatus = when (currentStatus) {
            "PLACED" -> "STORE_CONFIRMED"
            "STORE_CONFIRMED" -> "PREPARING"
            "PREPARING" -> "RIDER_ASSIGNED"
            "RIDER_ASSIGNED" -> "OUT_FOR_DELIVERY"
            "OUT_FOR_DELIVERY" -> "DELIVERED"
            else -> currentStatus
        }
        db.orderDao().updateOrderStatus(orderId, nextStatus)
        nextStatus
    }

    suspend fun cancelOrder(orderId: String) = withContext(Dispatchers.IO) {
        db.orderDao().updateOrderStatus(orderId, "CANCELLED")
    }

    suspend fun addReview(targetType: String, targetId: String, author: String, rating: Float, comment: String) = withContext(Dispatchers.IO) {
        val dateText = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
        db.reviewDao().insertReview(
            ReviewEntity(
                targetType = targetType,
                targetId = targetId,
                author = author,
                rating = rating,
                comment = comment,
                dateText = dateText
            )
        )
    }

    // Merchant / Store Owner operations
    suspend fun updateProductStock(productId: String, inStock: Boolean, stockCount: Int) = withContext(Dispatchers.IO) {
        db.productDao().updateStock(productId, inStock, stockCount)
    }

    suspend fun updateProductPrice(productId: String, newPrice: Double) = withContext(Dispatchers.IO) {
        db.productDao().updatePrice(productId, newPrice)
    }

    suspend fun addMerchantProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        db.productDao().insertProduct(product)
    }

    suspend fun updateStoreStatus(storeId: String, isOpen: Boolean) = withContext(Dispatchers.IO) {
        db.storeDao().updateStoreStatus(storeId, isOpen)
    }

    suspend fun addAddress(address: AddressEntity) = withContext(Dispatchers.IO) {
        db.addressDao().insertAddress(address)
    }

    suspend fun getProductDirect(productId: String): ProductEntity? = withContext(Dispatchers.IO) {
        db.productDao().getProductByIdDirect(productId)
    }
}

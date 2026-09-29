package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val rating: Float,
    val reviewCount: Int,
    val distanceKm: Float,
    val deliveryTimeMinutes: Int,
    val deliveryFee: Double,
    val minOrder: Double,
    val address: String,
    val promoTag: String,
    val isOpen: Boolean = true,
    val bannerColorHex: String = "#4F46E5",
    val description: String = ""
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val storeId: String,
    val storeName: String,
    val name: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val rating: Float,
    val reviewCount: Int,
    val description: String,
    val specifications: String, // Key-value pairs stored as formatted string
    val inStock: Boolean = true,
    val stockCount: Int = 25,
    val isBestseller: Boolean = false,
    val isRecommended: Boolean = false,
    val badge: String = "",
    val accentColorHex: String = "#0EA5E9"
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val storeId: String,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val storeId: String,
    val storeName: String,
    val itemsSummary: String,
    val totalAmount: Double,
    val status: String, // PLACED, STORE_CONFIRMED, PREPARING, RIDER_ASSIGNED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    val placedAtTimestamp: Long,
    val deliveryAddress: String,
    val paymentMethod: String,
    val deliveryPartnerName: String,
    val deliveryPartnerPhone: String,
    val deliveryPartnerVehicle: String,
    val deliveryOtp: String,
    val estimatedMinutes: Int
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: String,
    val label: String, // Home, Work, Other
    val street: String,
    val city: String,
    val postalCode: String,
    val isDefault: Boolean = false
)

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: String, // "product" or "store"
    val targetId: String,
    val author: String,
    val rating: Float,
    val comment: String,
    val dateText: String
)

package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.SeedData
import com.example.data.model.*
import com.example.data.repository.OmniRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppPersona {
    CUSTOMER,
    STORE_OWNER,
    DELIVERY_PARTNER,
    ADMIN
}

sealed class ScreenRoute {
    object Home : ScreenRoute()
    data class StoreDetail(val storeId: String) : ScreenRoute()
    data class ProductDetail(val productId: String) : ScreenRoute()
    object Cart : ScreenRoute()
    data class OrderTracking(val orderId: String) : ScreenRoute()
    data class CategoryFilter(val category: String) : ScreenRoute()
    object OrderHistory : ScreenRoute()
    object Wishlist : ScreenRoute()
    object Profile : ScreenRoute()
    object CustomerSupport : ScreenRoute()
    object AddressManager : ScreenRoute()
}

data class CartItemDetailed(
    val product: ProductEntity,
    val quantity: Int
)

data class CartSummary(
    val items: List<CartItemDetailed> = emptyList(),
    val store: StoreEntity? = null,
    val itemTotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val packagingFee: Double = 1.00,
    val discount: Double = 0.0,
    val totalPayable: Double = 0.0,
    val appliedCoupon: String? = null,
    val freeDeliveryShortfall: Double = 0.0
)

class OmniViewModel(private val repository: OmniRepository) : ViewModel() {

    // Persona
    private val _currentPersona = MutableStateFlow(AppPersona.CUSTOMER)
    val currentPersona: StateFlow<AppPersona> = _currentPersona.asStateFlow()

    // Navigation Stack
    private val _screenStack = MutableStateFlow<List<ScreenRoute>>(listOf(ScreenRoute.Home))
    val currentScreen: StateFlow<ScreenRoute> = _screenStack.map { it.lastOrNull() ?: ScreenRoute.Home }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenRoute.Home)

    // Data streams
    val stores: StateFlow<List<StoreEntity>> = repository.allStores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawCartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val addresses: StateFlow<List<AddressEntity>> = repository.addresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistIds: StateFlow<List<String>> = repository.wishlistIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active location / selected address
    private val _selectedAddress = MutableStateFlow<AddressEntity?>(null)
    val selectedAddress: StateFlow<AddressEntity?> = _selectedAddress.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Selected category for filtering
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Applied coupon
    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    // Order tracking auto-simulation toggle
    private val _isTrackingLiveSimulation = MutableStateFlow(true)
    val isTrackingLiveSimulation: StateFlow<Boolean> = _isTrackingLiveSimulation.asStateFlow()

    // Delivery Partner state
    private val _isRiderOnline = MutableStateFlow(true)
    val isRiderOnline: StateFlow<Boolean> = _isRiderOnline.asStateFlow()

    private val _riderEarningsToday = MutableStateFlow(68.50)
    val riderEarningsToday: StateFlow<Double> = _riderEarningsToday.asStateFlow()

    // Store Owner state
    private val _merchantSelectedStoreId = MutableStateFlow("store_apex_tech")
    val merchantSelectedStoreId: StateFlow<String> = _merchantSelectedStoreId.asStateFlow()

    // Recently viewed product IDs
    private val _recentlyViewedIds = MutableStateFlow<List<String>>(listOf("prod_usb_c_charger", "prod_first_aid_kit"))
    val recentlyViewedIds: StateFlow<List<String>> = _recentlyViewedIds.asStateFlow()

    // Detailed Cart calculation
    val cartSummary: StateFlow<CartSummary> = combine(
        rawCartItems,
        allProducts,
        stores,
        _appliedCoupon
    ) { cartItems, products, storeList, couponCode ->
        if (cartItems.isEmpty()) {
            return@combine CartSummary()
        }

        val prodMap = products.associateBy { it.id }
        val detailedItems = cartItems.mapNotNull { cartItem ->
            val p = prodMap[cartItem.productId]
            if (p != null) CartItemDetailed(p, cartItem.quantity) else null
        }

        val storeId = cartItems.firstOrNull()?.storeId
        val store = storeList.find { it.id == storeId }

        val itemTotal = detailedItems.sumOf { it.product.price * it.quantity }
        val baseDelivery = store?.deliveryFee ?: 1.99

        var discount = 0.0
        var effectiveDelivery = baseDelivery

        if (couponCode != null) {
            val coupon = SeedData.COUPONS.find { it.code.equals(couponCode, ignoreCase = true) }
            if (coupon != null && itemTotal >= coupon.minOrder) {
                if (coupon.code == "QUICKFREE") {
                    effectiveDelivery = 0.0
                    discount = baseDelivery
                } else {
                    val calc = itemTotal * (coupon.discountPercent / 100.0)
                    discount = calc.coerceAtMost(coupon.maxDiscount)
                }
            }
        }

        val packaging = 1.00
        val total = (itemTotal + effectiveDelivery + packaging - discount).coerceAtLeast(0.0)
        val freeDeliveryShortfall = (30.0 - itemTotal).coerceAtLeast(0.0)

        CartSummary(
            items = detailedItems,
            store = store,
            itemTotal = itemTotal,
            deliveryFee = effectiveDelivery,
            packagingFee = packaging,
            discount = discount,
            totalPayable = total,
            appliedCoupon = couponCode,
            freeDeliveryShortfall = freeDeliveryShortfall
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartSummary())

    init {
        // Set default address
        viewModelScope.launch {
            addresses.collect { list ->
                if (_selectedAddress.value == null && list.isNotEmpty()) {
                    _selectedAddress.value = list.firstOrNull { it.isDefault } ?: list.first()
                }
            }
        }
    }

    // Persona Navigation
    fun setPersona(persona: AppPersona) {
        _currentPersona.value = persona
        _screenStack.value = listOf(ScreenRoute.Home)
    }

    // Navigation Actions
    fun navigateTo(route: ScreenRoute) {
        val current = _screenStack.value.toMutableList()
        // Record recently viewed
        if (route is ScreenRoute.ProductDetail) {
            val existing = _recentlyViewedIds.value.toMutableList()
            existing.remove(route.productId)
            existing.add(0, route.productId)
            _recentlyViewedIds.value = existing.take(6)
        }
        current.add(route)
        _screenStack.value = current
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value.toMutableList()
        if (current.size > 1) {
            current.removeAt(current.size - 1)
            _screenStack.value = current
            return true
        }
        return false
    }

    fun popToRoot() {
        _screenStack.value = listOf(ScreenRoute.Home)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(cat: String?) {
        _selectedCategory.value = cat
    }

    fun selectAddress(address: AddressEntity) {
        _selectedAddress.value = address
    }

    // Cart Operations
    fun addToCart(product: ProductEntity, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product.id, product.storeId, quantity)
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, quantity)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun applyCoupon(code: String): Boolean {
        val coupon = SeedData.COUPONS.find { it.code.equals(code, ignoreCase = true) }
        return if (coupon != null) {
            _appliedCoupon.value = coupon.code
            true
        } else {
            false
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    // Wishlist
    fun toggleWishlist(productId: String) {
        viewModelScope.launch {
            repository.toggleWishlist(productId)
        }
    }

    // Place Order
    fun placeOrder(
        paymentMethod: String,
        onSuccess: (String) -> Unit
    ) {
        val summary = cartSummary.value
        val store = summary.store ?: return
        val addressText = _selectedAddress.value?.let { "${it.label}: ${it.street}, ${it.city}" }
            ?: "742 Evergreen Terrace, Apt 4B"

        val items = summary.items.map { it.product to it.quantity }

        viewModelScope.launch {
            val orderId = repository.placeOrder(
                items = items,
                store = store,
                deliveryAddress = addressText,
                paymentMethod = paymentMethod,
                totalAmount = summary.totalPayable
            )
            _appliedCoupon.value = null
            navigateTo(ScreenRoute.OrderTracking(orderId))
            onSuccess(orderId)
        }
    }

    // Advance Order Status (for simulation and Rider/Store actions)
    fun advanceOrderStatus(orderId: String, currentStatus: String) {
        viewModelScope.launch {
            repository.advanceOrderStatus(orderId, currentStatus)
        }
    }

    fun toggleTrackingLiveSimulation() {
        _isTrackingLiveSimulation.value = !_isTrackingLiveSimulation.value
    }

    // Reviews
    fun submitReview(targetType: String, targetId: String, author: String, rating: Float, comment: String) {
        viewModelScope.launch {
            repository.addReview(targetType, targetId, author, rating, comment)
        }
    }

    // Rider Controls
    fun toggleRiderOnline() {
        _isRiderOnline.value = !_isRiderOnline.value
    }

    // Merchant Controls
    fun setMerchantSelectedStore(storeId: String) {
        _merchantSelectedStoreId.value = storeId
    }

    fun updateProductStock(productId: String, inStock: Boolean, stockCount: Int) {
        viewModelScope.launch {
            repository.updateProductStock(productId, inStock, stockCount)
        }
    }

    fun updateProductPrice(productId: String, newPrice: Double) {
        viewModelScope.launch {
            repository.updateProductPrice(productId, newPrice)
        }
    }

    fun addMerchantProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.addMerchantProduct(product)
        }
    }

    fun toggleStoreOpen(storeId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.updateStoreStatus(storeId, !currentStatus)
        }
    }

    fun addNewAddress(label: String, street: String, city: String, postalCode: String) {
        viewModelScope.launch {
            val id = "addr_" + System.currentTimeMillis()
            val entity = AddressEntity(id, label, street, city, postalCode, false)
            repository.addAddress(entity)
        }
    }
}

class OmniViewModelFactory(private val repository: OmniRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OmniViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OmniViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

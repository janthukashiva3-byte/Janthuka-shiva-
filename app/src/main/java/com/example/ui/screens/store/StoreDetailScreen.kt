package com.example.ui.screens.store

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProductCard
import com.example.ui.theme.OmniAccentGreen
import com.example.ui.viewmodel.OmniViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    storeId: String,
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val stores by viewModel.stores.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val rawCart by viewModel.rawCartItems.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()

    val store = stores.find { it.id == storeId }
    val storeProducts = remember(allProducts, storeId) {
        allProducts.filter { it.storeId == storeId }
    }

    var storeSearchQuery by remember { mutableStateOf("") }
    var selectedStoreCategory by remember { mutableStateOf("All") }

    val cartQuantities = remember(rawCart) {
        rawCart.associate { it.productId to it.quantity }
    }

    val storeCategories = remember(storeProducts) {
        listOf("All") + storeProducts.map { it.category }.distinct()
    }

    val filteredProducts = remember(storeProducts, storeSearchQuery, selectedStoreCategory) {
        storeProducts.filter { p ->
            val matchesSearch = storeSearchQuery.isBlank() ||
                    p.name.contains(storeSearchQuery, ignoreCase = true) ||
                    p.description.contains(storeSearchQuery, ignoreCase = true)

            val matchesCat = selectedStoreCategory == "All" || p.category == selectedStoreCategory
            matchesSearch && matchesCat
        }
    }

    if (store == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val bannerColor = remember(store.bannerColorHex) {
        try {
            Color(android.graphics.Color.parseColor(store.bannerColorHex))
        } catch (e: Exception) {
            Color(0xFF4F46E5)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(store.name, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("store_detail_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(ScreenRoute.Cart) }) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Store Hero Header Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(bannerColor, bannerColor.copy(alpha = 0.8f))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = store.name,
                                    tint = bannerColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    // Status Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (store.isOpen) Color(0xFF10B981) else Color(0xFFEF4444),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = if (store.isOpen) "Open Now" else "Closed",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Store Info Card
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = store.name,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = store.description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick info chips
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = OmniAccentGreen,
                                contentColor = Color.White
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "%.1f".format(store.rating),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp))
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${store.deliveryTimeMinutes} mins",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "•  ${store.distanceKm} km away",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "•  Fee: $${store.deliveryFee}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (store.promoTag.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalOffer,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = store.promoTag,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Search inside store
                        OutlinedTextField(
                            value = storeSearchQuery,
                            onValueChange = { storeSearchQuery = it },
                            placeholder = { Text("Search in ${store.name}...", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // In-Store Category Chips
            item {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(storeCategories) { cat ->
                        FilterChip(
                            selected = selectedStoreCategory == cat,
                            onClick = { selectedStoreCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Store Products Grid
            val chunked = filteredProducts.chunked(2)
            items(chunked) { rowProds ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    for (prod in rowProds) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProductCard(
                                product = prod,
                                cartQuantity = cartQuantities[prod.id] ?: 0,
                                isWishlisted = wishlistIds.contains(prod.id),
                                onProductClick = {
                                    viewModel.navigateTo(ScreenRoute.ProductDetail(prod.id))
                                },
                                onAddToCart = {
                                    viewModel.addToCart(prod, 1)
                                },
                                onIncrement = {
                                    val current = cartQuantities[prod.id] ?: 0
                                    viewModel.updateCartQuantity(prod.id, current + 1)
                                },
                                onDecrement = {
                                    val current = cartQuantities[prod.id] ?: 0
                                    viewModel.updateCartQuantity(prod.id, current - 1)
                                },
                                onToggleWishlist = {
                                    viewModel.toggleWishlist(prod.id)
                                }
                            )
                        }
                    }
                    if (rowProds.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

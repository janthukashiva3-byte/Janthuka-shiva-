package com.example.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SeedData
import com.example.data.model.AddressEntity
import com.example.data.model.ProductEntity
import com.example.data.model.StoreEntity
import com.example.ui.components.*
import com.example.ui.viewmodel.OmniViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val stores by viewModel.stores.collectAsState()
    val products by viewModel.allProducts.collectAsState()
    val rawCart by viewModel.rawCartItems.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val allAddresses by viewModel.addresses.collectAsState()
    val currentPersona by viewModel.currentPersona.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recentlyViewedIds by viewModel.recentlyViewedIds.collectAsState()
    val pastOrders by viewModel.orders.collectAsState()

    var showAddressDialog by remember { mutableStateOf(false) }
    var activeCategoryFilter by remember { mutableStateOf<String?>(null) }

    val cartQuantities = remember(rawCart) {
        rawCart.associate { it.productId to it.quantity }
    }

    val categories = listOf(
        "All" to Icons.Default.GridView,
        "Groceries" to Icons.Default.LocalGroceryStore,
        "Electronics" to Icons.Default.Devices,
        "Pharmacy" to Icons.Default.Medication,
        "Beauty" to Icons.Default.AutoAwesome,
        "Fashion" to Icons.Default.Checkroom,
        "Home & Kitchen" to Icons.Default.Home,
        "Books" to Icons.Default.MenuBook,
        "Pet Supplies" to Icons.Default.Pets,
        "Hardware" to Icons.Default.Build
    )

    // Filter products based on search or category
    val filteredProducts = remember(products, searchQuery, activeCategoryFilter) {
        products.filter { p ->
            val matchesQuery = searchQuery.isBlank() ||
                    p.name.contains(searchQuery, ignoreCase = true) ||
                    p.category.contains(searchQuery, ignoreCase = true) ||
                    p.storeName.contains(searchQuery, ignoreCase = true)

            val matchesCat = activeCategoryFilter == null || activeCategoryFilter == "All" ||
                    p.category.contains(activeCategoryFilter!!, ignoreCase = true)

            matchesQuery && matchesCat
        }
    }

    val filteredStores = remember(stores, searchQuery, activeCategoryFilter) {
        stores.filter { s ->
            val matchesQuery = searchQuery.isBlank() ||
                    s.name.contains(searchQuery, ignoreCase = true) ||
                    s.category.contains(searchQuery, ignoreCase = true)

            val matchesCat = activeCategoryFilter == null || activeCategoryFilter == "All" ||
                    s.category.contains(activeCategoryFilter!!, ignoreCase = true)

            matchesQuery && matchesCat
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen_column")
    ) {
        // 1. Header with Location Selector & Persona switcher
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Location selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showAddressDialog = true }
                            .padding(vertical = 4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedAddress?.label ?: "Delivery Location",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = selectedAddress?.street ?: "742 Evergreen Terrace, Downtown",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Persona Mode Switcher
                    PersonaHeaderChip(
                        currentPersona = currentPersona,
                        onSelectPersona = { viewModel.setPersona(it) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            "Search products, stores & categories...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input")
                )
            }
        }

        // 2. Promotional Banners (Only show when not actively searching)
        if (searchQuery.isBlank()) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SeedData.COUPONS) { coupon ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .width(280.dp)
                                .height(120.dp)
                                .clickable {
                                    viewModel.applyCoupon(coupon.code)
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.secondary
                                            )
                                        )
                                    )
                                    .padding(14.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White.copy(alpha = 0.25f)
                                        ) {
                                            Text(
                                                text = coupon.code,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        Text(
                                            text = "Tap to Apply",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = coupon.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = coupon.description,
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.9f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Popular Categories Row
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Explore Categories",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { (catName, icon) ->
                        val isSelected = (activeCategoryFilter == catName) || (catName == "All" && activeCategoryFilter == null)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                activeCategoryFilter = if (catName == "All") null else catName
                            },
                            label = { Text(catName, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = catName,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // 4. "Order Again" Section (if user has delivered orders and not searching)
        if (searchQuery.isBlank() && pastOrders.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Order Again",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Past orders",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                viewModel.navigateTo(ScreenRoute.OrderHistory)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pastOrders.take(3)) { pastOrder ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier
                                    .width(260.dp)
                                    .clickable {
                                        viewModel.navigateTo(ScreenRoute.OrderTracking(pastOrder.orderId))
                                    }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = pastOrder.storeName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFDCFCE7)
                                        ) {
                                            Text(
                                                text = pastOrder.status,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF166534),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = pastOrder.itemsSummary,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "$%.2f".format(pastOrder.totalAmount),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                        TextButton(
                                            onClick = {
                                                viewModel.navigateTo(ScreenRoute.OrderTracking(pastOrder.orderId))
                                            },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Track / View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Nearby Stores Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    Text(
                        text = "Nearby Stores",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Delivering in 12–25 minutes",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredStores) { store ->
                    StoreCard(
                        store = store,
                        onClick = {
                            viewModel.navigateTo(ScreenRoute.StoreDetail(store.id))
                        },
                        modifier = Modifier.width(260.dp)
                    )
                }
            }
        }

        // 6. Recommended Products Grid Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    Text(
                        text = if (searchQuery.isNotBlank()) "Search Results (${filteredProducts.size})" else "Recommended For You",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Everyday essentials & gadgets",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 2-column product layout
        val chunkedProducts = filteredProducts.chunked(2)
        items(chunkedProducts) { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                for (prod in rowItems) {
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
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Extra padding at bottom for navigation bar / floating cart
        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }

    // Address Selection Dialog
    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Choose Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    allAddresses.forEach { addr ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedAddress?.id == addr.id) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.selectAddress(addr)
                                    showAddressDialog = false
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (addr.label == "Home") Icons.Default.Home else Icons.Default.Business,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = addr.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "${addr.street}, ${addr.city}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}

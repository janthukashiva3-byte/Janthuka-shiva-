package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.data.model.StoreEntity
import com.example.ui.theme.OmniAccentGreen
import com.example.ui.viewmodel.AppPersona

@Composable
fun PersonaHeaderChip(
    currentPersona: AppPersona,
    onSelectPersona: (AppPersona) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = when (currentPersona) {
                AppPersona.CUSTOMER -> MaterialTheme.colorScheme.primaryContainer
                AppPersona.STORE_OWNER -> Color(0xFFFEF3C7) // Amber
                AppPersona.DELIVERY_PARTNER -> Color(0xFFDCFCE7) // Emerald
                AppPersona.ADMIN -> Color(0xFFF3E8FF) // Purple
            },
            modifier = Modifier
                .testTag("persona_switcher_chip")
                .clickable { expanded = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = when (currentPersona) {
                        AppPersona.CUSTOMER -> Icons.Default.ShoppingBag
                        AppPersona.STORE_OWNER -> Icons.Default.Storefront
                        AppPersona.DELIVERY_PARTNER -> Icons.Default.ElectricScooter
                        AppPersona.ADMIN -> Icons.Default.AdminPanelSettings
                    },
                    contentDescription = "Current Mode",
                    tint = when (currentPersona) {
                        AppPersona.CUSTOMER -> MaterialTheme.colorScheme.primary
                        AppPersona.STORE_OWNER -> Color(0xFFB45309)
                        AppPersona.DELIVERY_PARTNER -> Color(0xFF15803D)
                        AppPersona.ADMIN -> Color(0xFF7E22CE)
                    },
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (currentPersona) {
                        AppPersona.CUSTOMER -> "Customer"
                        AppPersona.STORE_OWNER -> "Merchant"
                        AppPersona.DELIVERY_PARTNER -> "Rider"
                        AppPersona.ADMIN -> "Admin"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (currentPersona) {
                        AppPersona.CUSTOMER -> MaterialTheme.colorScheme.onPrimaryContainer
                        AppPersona.STORE_OWNER -> Color(0xFF78350F)
                        AppPersona.DELIVERY_PARTNER -> Color(0xFF14532D)
                        AppPersona.ADMIN -> Color(0xFF581C87)
                    }
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Switch Mode",
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = { Text("🛒 Customer Marketplace", fontWeight = FontWeight.SemiBold) },
                onClick = {
                    onSelectPersona(AppPersona.CUSTOMER)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("🏪 Store Owner (Merchant)", fontWeight = FontWeight.SemiBold) },
                onClick = {
                    onSelectPersona(AppPersona.STORE_OWNER)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("🚴 Delivery Partner (Rider)", fontWeight = FontWeight.SemiBold) },
                onClick = {
                    onSelectPersona(AppPersona.DELIVERY_PARTNER)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("🛡️ Admin Console", fontWeight = FontWeight.SemiBold) },
                onClick = {
                    onSelectPersona(AppPersona.ADMIN)
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun CategoryIcon(category: String): ImageVector {
    return when {
        category.contains("Groceries", ignoreCase = true) -> Icons.Default.LocalGroceryStore
        category.contains("Pharmacy", ignoreCase = true) || category.contains("Health", ignoreCase = true) -> Icons.Default.Medication
        category.contains("Electronics", ignoreCase = true) -> Icons.Default.Devices
        category.contains("Fashion", ignoreCase = true) -> Icons.Default.Checkroom
        category.contains("Beauty", ignoreCase = true) -> Icons.Default.AutoAwesome
        category.contains("Home", ignoreCase = true) -> Icons.Default.Home
        category.contains("Books", ignoreCase = true) -> Icons.Default.MenuBook
        category.contains("Gifts", ignoreCase = true) -> Icons.Default.CardGiftcard
        category.contains("Pet", ignoreCase = true) -> Icons.Default.Pets
        category.contains("Hardware", ignoreCase = true) -> Icons.Default.Build
        else -> Icons.Default.Category
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    cartQuantity: Int,
    isWishlisted: Boolean,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onToggleWishlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}")
            .clickable { onProductClick() }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Product Visual Placeholder with gradient and category emblem
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Category icon emblem
                Icon(
                    imageVector = CategoryIcon(product.category),
                    contentDescription = product.name,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.size(44.dp)
                )

                // Badge top-left
                if (product.badge.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = product.badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Wishlist heart top-right
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Toggle Wishlist",
                        tint = if (isWishlisted) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Stock warning if low
                if (product.stockCount <= 15) {
                    Surface(
                        shape = RoundedCornerShape(topStart = 8.dp),
                        color = Color(0xFFFEF2F2),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = "${product.stockCount} left",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB91C1C),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Store Name & Rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = product.storeName,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "%.1f".format(product.rating),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Product Name
            Text(
                text = product.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp,
                modifier = Modifier.height(34.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Price and Action Stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "$%.2f".format(product.price),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (product.originalPrice > product.price) {
                        Text(
                            text = "$%.2f".format(product.originalPrice),
                            fontSize = 11.sp,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (cartQuantity == 0) {
                    Button(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("add_to_cart_${product.id}")
                    ) {
                        Text("ADD", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(2.dp)
                    ) {
                        IconButton(
                            onClick = onDecrement,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "$cartQuantity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        IconButton(
                            onClick = onIncrement,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoreCard(
    store: StoreEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("store_card_${store.id}")
            .clickable { onClick() }
    ) {
        Column {
            // Store banner simulation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(android.graphics.Color.parseColor(store.bannerColorHex)),
                                Color(android.graphics.Color.parseColor(store.bannerColorHex)).copy(alpha = 0.7f)
                            )
                        )
                    )
                    .padding(12.dp)
            ) {
                // Store logo badge
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = store.name,
                            tint = Color(android.graphics.Color.parseColor(store.bannerColorHex)),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Delivery time pill top-right
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Delivery time",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${store.deliveryTimeMinutes} mins",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Store Info
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = store.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = OmniAccentGreen,
                        contentColor = Color.White
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "%.1f".format(store.rating),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${store.category} • ${store.distanceKm} km away",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Delivery: $${store.deliveryFee} • Min order $${store.minOrder}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (store.promoTag.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = store.promoTag,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

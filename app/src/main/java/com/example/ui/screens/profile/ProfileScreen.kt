package com.example.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SeedData
import com.example.ui.components.CategoryIcon
import com.example.ui.theme.OmniAccentGreen
import com.example.ui.viewmodel.OmniViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val addresses by viewModel.addresses.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()

    var showAddAddressDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showWalletTopUpDialog by remember { mutableStateOf(false) }
    var walletBalance by remember { mutableStateOf(45.00) }

    // Add address form state
    var newAddressLabel by remember { mutableStateOf("Home") }
    var newAddressStreet by remember { mutableStateOf("") }
    var newAddressCity by remember { mutableStateOf("Metro City") }
    var newAddressPostal by remember { mutableStateOf("94103") }

    val wishlistedProducts = remember(wishlistIds, allProducts) {
        allProducts.filter { wishlistIds.contains(it.id) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Account", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
            // User Header Card
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(60.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User Avatar",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Shiva K.", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("+1 (555) 392-4910 • Verified", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = "OmniDrop Plus Member ⚡",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Wallet & Quick Balances Card
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column {
                            Text("OmniDrop Wallet", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$%.2f".format(walletBalance), fontWeight = FontWeight.Black, fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Button(
                            onClick = { showWalletTopUpDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Money", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Wishlist Quick Access
            if (wishlistedProducts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Saved in Wishlist (${wishlistedProducts.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(wishlistedProducts) { wishProd ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { viewModel.navigateTo(ScreenRoute.ProductDetail(wishProd.id)) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = CategoryIcon(wishProd.category), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(wishProd.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                Text("$%.2f • ${wishProd.storeName}".format(wishProd.price), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { viewModel.addToCart(wishProd, 1) }) {
                                Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = "Add to Cart", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            // Saved Addresses Management
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Saved Delivery Addresses", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    TextButton(onClick = { showAddAddressDialog = true }) {
                        Icon(imageVector = Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add New", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(addresses) { addr ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(
                            imageVector = if (addr.label == "Home") Icons.Default.Home else Icons.Default.Work,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(addr.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${addr.street}, ${addr.city} ${addr.postalCode}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        RadioButton(
                            selected = addr.isDefault,
                            onClick = { viewModel.selectAddress(addr) }
                        )
                    }
                }
            }

            // Quick Links: Orders, Support, Coupons
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Past Orders & Invoices", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                            supportingContent = { Text("${orders.size} orders placed", fontSize = 11.sp) },
                            leadingContent = { Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            trailingContent = { Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null) },
                            modifier = Modifier.clickable { viewModel.navigateTo(ScreenRoute.OrderHistory) }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("Available Coupons", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                            supportingContent = { Text("View ${SeedData.COUPONS.size} active promo codes", fontSize = 11.sp) },
                            leadingContent = { Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            trailingContent = { Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null) },
                            modifier = Modifier.clickable { viewModel.popToRoot() }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("Customer Support & Help", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                            supportingContent = { Text("24/7 instant chat and order assistance", fontSize = 11.sp) },
                            leadingContent = { Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            trailingContent = { Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null) },
                            modifier = Modifier.clickable { showSupportDialog = true }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }

    // Add Address Dialog
    if (showAddAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newAddressLabel,
                        onValueChange = { newAddressLabel = it },
                        label = { Text("Label (e.g. Home, Office, Gym)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newAddressStreet,
                        onValueChange = { newAddressStreet = it },
                        label = { Text("Street Address & Unit #") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newAddressCity,
                            onValueChange = { newAddressCity = it },
                            label = { Text("City") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newAddressPostal,
                            onValueChange = { newAddressPostal = it },
                            label = { Text("Postal Code") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAddressStreet.isNotBlank()) {
                            viewModel.addNewAddress(newAddressLabel, newAddressStreet, newAddressCity, newAddressPostal)
                            newAddressStreet = ""
                            showAddAddressDialog = false
                        }
                    }
                ) {
                    Text("Save Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Wallet Top Up Dialog
    if (showWalletTopUpDialog) {
        AlertDialog(
            onDismissRequest = { showWalletTopUpDialog = false },
            title = { Text("Top Up OmniDrop Wallet", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select amount to add via linked payment:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { walletBalance += 20.0; showWalletTopUpDialog = false }) { Text("+$20") }
                        Button(onClick = { walletBalance += 50.0; showWalletTopUpDialog = false }) { Text("+$50") }
                        Button(onClick = { walletBalance += 100.0; showWalletTopUpDialog = false }) { Text("+$100") }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showWalletTopUpDialog = false }) { Text("Close") }
            }
        )
    }

    // Customer Support Dialog
    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = { Text("24/7 OmniDrop Helpdesk", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("How can we assist you today?", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("• Where is my order? -> Real-time GPS tracking is available on the Tracking Screen.", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Cancellation policy: Instant full refund before rider pick-up.", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Defective item: Upload photo in Orders for 10-minute replacement.", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFDCFCE7), modifier = Modifier.fillMaxWidth()) {
                        Text("Live Support Representative is Online", color = Color(0xFF166534), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showSupportDialog = false }) { Text("Got It") }
            }
        )
    }
}

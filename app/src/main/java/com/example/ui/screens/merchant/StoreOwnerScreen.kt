package com.example.ui.screens.merchant

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
import com.example.data.model.ProductEntity
import com.example.ui.components.CategoryIcon
import com.example.ui.components.PersonaHeaderChip
import com.example.ui.theme.OmniAccentGreen
import com.example.ui.viewmodel.AppPersona
import com.example.ui.viewmodel.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreOwnerScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val stores by viewModel.stores.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val merchantStoreId by viewModel.merchantSelectedStoreId.collectAsState()
    val currentPersona by viewModel.currentPersona.collectAsState()

    val currentStore = stores.find { it.id == merchantStoreId } ?: stores.firstOrNull()
    val storeProducts = allProducts.filter { it.storeId == (currentStore?.id ?: "") }
    val storeOrders = orders.filter { it.storeId == (currentStore?.id ?: "") }

    var showAddProductDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Live Orders, 1: Inventory & Catalog, 2: Analytics

    // Add Product Form fields
    var newProdName by remember { mutableStateOf("") }
    var newProdCategory by remember { mutableStateOf("Electronics & Accessories") }
    var newProdPrice by remember { mutableStateOf("") }
    var newProdStock by remember { mutableStateOf("20") }
    var newProdDesc by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(currentStore?.name ?: "Merchant Portal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = if (currentStore?.isOpen == true) "Store is Live & Accepting Orders" else "Store is currently Paused",
                            fontSize = 11.sp,
                            color = if (currentStore?.isOpen == true) OmniAccentGreen else MaterialTheme.colorScheme.error
                        )
                    }
                },
                actions = {
                    if (currentStore != null) {
                        Switch(
                            checked = currentStore.isOpen,
                            onCheckedChange = { viewModel.toggleStoreOpen(currentStore.id, currentStore.isOpen) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    PersonaHeaderChip(
                        currentPersona = currentPersona,
                        onSelectPersona = { viewModel.setPersona(it) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showAddProductDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Product")
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Store Selection Switcher dropdown / chip
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Active Store:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            stores.take(3).forEach { s ->
                                FilterChip(
                                    selected = s.id == currentStore?.id,
                                    onClick = { viewModel.setMerchantSelectedStore(s.id) },
                                    label = { Text(s.name.take(15) + "...", fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Tabs: 0: Live Orders, 1: Inventory, 2: Sales
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Live Orders (${storeOrders.count { it.status != "DELIVERED" }})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Inventory (${storeProducts.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Sales Analytics", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Tab 0: Live Orders
            if (selectedTab == 0) {
                if (storeOrders.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No incoming orders yet for this store.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(storeOrders) { ord ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Order #${ord.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (ord.status == "DELIVERED") Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            text = ord.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (ord.status == "DELIVERED") Color(0xFF166534) else Color(0xFFB45309),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Items: ${ord.itemsSummary}", fontSize = 12.sp)
                                Text("Total: $%.2f • Deliver to: ${ord.deliveryAddress}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(10.dp))

                                if (ord.status != "DELIVERED") {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                viewModel.advanceOrderStatus(ord.orderId, ord.status)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = when (ord.status) {
                                                    "PLACED" -> "Accept & Confirm Order"
                                                    "STORE_CONFIRMED" -> "Start Packing Items"
                                                    "PREPARING" -> "Mark Ready for Rider"
                                                    else -> "Progress: ${ord.status}"
                                                },
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 1: Inventory Management
            if (selectedTab == 1) {
                items(storeProducts) { prod ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(prod.category, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Text(
                                    text = "$%.2f".format(prod.price),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("In Stock:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Switch(
                                        checked = prod.inStock,
                                        onCheckedChange = { checked ->
                                            viewModel.updateProductStock(prod.id, checked, if (checked) 25 else 0)
                                        }
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Stock: ${prod.stockCount} units", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            viewModel.updateProductStock(prod.id, true, prod.stockCount + 10)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add 10", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 2: Sales Analytics
            if (selectedTab == 2) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Total Gross Revenue", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$1,842.50", fontWeight = FontWeight.Black, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Orders Fulfilled", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("64 Orders", fontWeight = FontWeight.Black, fontSize = 20.sp, color = OmniAccentGreen)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Average Order Value (AOV)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$28.78", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Customer Repeat Rate: 42.6%", fontSize = 12.sp)
                                Text("Store Rating: 4.8 ★ (${currentStore?.reviewCount} reviews)", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Add Product Dialog
    if (showAddProductDialog) {
        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Add New Product", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newProdName,
                        onValueChange = { newProdName = it },
                        label = { Text("Product Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newProdCategory,
                        onValueChange = { newProdCategory = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newProdPrice,
                            onValueChange = { newProdPrice = it },
                            label = { Text("Price ($)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newProdStock,
                            onValueChange = { newProdStock = it },
                            label = { Text("Stock Qty") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newProdDesc,
                        onValueChange = { newProdDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val priceVal = newProdPrice.toDoubleOrNull() ?: 19.99
                        val stockVal = newProdStock.toIntOrNull() ?: 20
                        val newId = "prod_custom_" + System.currentTimeMillis()
                        val newEntity = ProductEntity(
                            id = newId,
                            storeId = currentStore?.id ?: "store_apex_tech",
                            storeName = currentStore?.name ?: "Apex Tech & Gadgets",
                            name = newProdName.ifBlank { "New Product Item" },
                            category = newProdCategory,
                            price = priceVal,
                            originalPrice = priceVal * 1.25,
                            rating = 5.0f,
                            reviewCount = 1,
                            description = newProdDesc.ifBlank { "High quality everyday essential." },
                            specifications = "Material: Premium Grade | Warranty: 1 Year",
                            inStock = true,
                            stockCount = stockVal,
                            isBestseller = false,
                            isRecommended = true,
                            badge = "New Arrival",
                            accentColorHex = "#4F46E5"
                        )
                        viewModel.addMerchantProduct(newEntity)
                        newProdName = ""
                        newProdPrice = ""
                        showAddProductDialog = false
                    }
                ) {
                    Text("Save Product")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) { Text("Cancel") }
            }
        )
    }
}

package com.example.ui.screens.admin

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
import com.example.ui.components.PersonaHeaderChip
import com.example.ui.theme.OmniAccentGreen
import com.example.ui.viewmodel.AppPersona
import com.example.ui.viewmodel.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val stores by viewModel.stores.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val currentPersona by viewModel.currentPersona.collectAsState()

    var adminSection by remember { mutableStateOf(0) } // 0: Overview & Analytics, 1: Stores & Fleet, 2: System Orders & Refunds, 3: Support Desk

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("OmniDrop Admin HQ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                actions = {
                    PersonaHeaderChip(
                        currentPersona = currentPersona,
                        onSelectPersona = { viewModel.setPersona(it) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
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
            // Navigation pills for Admin
            item {
                ScrollableTabRow(
                    selectedTabIndex = adminSection,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = adminSection == 0,
                        onClick = { adminSection = 0 },
                        text = { Text("Overview") }
                    )
                    Tab(
                        selected = adminSection == 1,
                        onClick = { adminSection = 1 },
                        text = { Text("Stores & Fleet (${stores.size})") }
                    )
                    Tab(
                        selected = adminSection == 2,
                        onClick = { adminSection = 2 },
                        text = { Text("Orders & Refunds") }
                    )
                    Tab(
                        selected = adminSection == 3,
                        onClick = { adminSection = 3 },
                        text = { Text("Complaints Desk") }
                    )
                }
            }

            // Section 0: Analytics Overview
            if (adminSection == 0) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Platform GMV (30d)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$48,920.00", fontWeight = FontWeight.Black, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("+18.4% MoM", fontSize = 10.sp, color = OmniAccentGreen, fontWeight = FontWeight.Bold)
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Active Fleet Riders", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("38 Live", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF0284C7))
                                    Text("Avg SLA: 14.2 min", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Catalog SKU Count", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${allProducts.size} SKUs", fontWeight = FontWeight.Black, fontSize = 18.sp)
                                    Text("Across 10 Categories", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Customer Satisfaction", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("4.85 ★", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFFF59E0B))
                                    Text("99.1% On-Time Delivery", fontSize = 10.sp, color = OmniAccentGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Active Platform Coupons
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Active Platform Promotions", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                SeedData.COUPONS.forEach { cp ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        Text(cp.code, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                        Text("${cp.discountPercent}% OFF (Max $${cp.maxDiscount})", fontSize = 11.sp)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFDCFCE7)
                                        ) {
                                            Text("Active", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 1: Store & Fleet oversight
            if (adminSection == 1) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Verified Merchant Stores", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(stores) { s ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(s.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${s.category} • ${s.address}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = { viewModel.toggleStoreOpen(s.id, s.isOpen) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (s.isOpen) MaterialTheme.colorScheme.error else OmniAccentGreen
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(if (s.isOpen) "Suspend" else "Activate", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Section 2: Orders & Refunds
            if (adminSection == 2) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("System Orders & Payments", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(orders) { ord ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Order #${ord.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("$%.2f".format(ord.totalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Store: ${ord.storeName} • Customer Addr: ${ord.deliveryAddress}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Status: ${ord.status}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                TextButton(
                                    onClick = {
                                        viewModel.advanceOrderStatus(ord.orderId, "DELIVERED")
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Force Complete", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Complaints Desk
            if (adminSection == 3) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ticket #TK-9024: Missing item check", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFEF3C7)) {
                                    Text("Under Review", fontSize = 10.sp, color = Color(0xFFB45309), modifier = Modifier.padding(4.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Customer reported missing cable in fast delivery #OD-98214. Store camera log confirmed packaging mistake.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { /* Instant refund simulated */ },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = OmniAccentGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Issue $11.49 Refund", fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = { /* Re-dispatch */ },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Re-dispatch Rider", fontSize = 11.sp)
                                }
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
}

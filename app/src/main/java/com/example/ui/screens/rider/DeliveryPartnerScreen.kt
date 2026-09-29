package com.example.ui.screens.rider

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PersonaHeaderChip
import com.example.ui.theme.OmniAccentGreen
import com.example.ui.viewmodel.AppPersona
import com.example.ui.viewmodel.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryPartnerScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val isOnline by viewModel.isRiderOnline.collectAsState()
    val earningsToday by viewModel.riderEarningsToday.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val currentPersona by viewModel.currentPersona.collectAsState()

    val activeTrip = orders.firstOrNull { it.status != "DELIVERED" && it.status != "CANCELLED" }
    var otpInput by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Rider Partner", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isOnline) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                        ) {
                            Text(
                                text = if (isOnline) "ONLINE" else "OFFLINE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOnline) Color(0xFF166534) else Color(0xFF991B1B),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    Switch(
                        checked = isOnline,
                        onCheckedChange = { viewModel.toggleRiderOnline() },
                        modifier = Modifier.padding(end = 8.dp)
                    )
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
            // Earnings Summary Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Today's Earnings", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text(
                            text = "$%.2f".format(earningsToday),
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("Completed Trips", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                Text("8 Trips", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Column {
                                Text("Tips Earned", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                Text("$14.50", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Column {
                                Text("Acceptance", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                Text("98.4%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // Current Assigned Delivery Task
            if (activeTrip != null && isOnline) {
                item {
                    Text(
                        text = "Active Delivery Request",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

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
                                Text("Order #${activeTrip.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = activeTrip.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB45309),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Pickup
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("PICKUP STORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(activeTrip.storeName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("Items: ${activeTrip.itemsSummary}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Drop-off
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = OmniAccentGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("DROP LOCATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OmniAccentGreen)
                                    Text(activeTrip.deliveryAddress, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("Customer Contact: Protected (${activeTrip.deliveryPartnerPhone})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Rider Status advance controls
                            when (activeTrip.status) {
                                "PLACED", "STORE_CONFIRMED" -> {
                                    Button(
                                        onClick = {
                                            viewModel.advanceOrderStatus(activeTrip.orderId, "PREPARING")
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Accept Delivery Request ($4.50 pay)")
                                    }
                                }
                                "PREPARING" -> {
                                    Button(
                                        onClick = {
                                            viewModel.advanceOrderStatus(activeTrip.orderId, "RIDER_ASSIGNED")
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Arrived at Store")
                                    }
                                }
                                "RIDER_ASSIGNED" -> {
                                    Button(
                                        onClick = {
                                            viewModel.advanceOrderStatus(activeTrip.orderId, "OUT_FOR_DELIVERY")
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Pick Up Package & Start Navigation")
                                    }
                                }
                                "OUT_FOR_DELIVERY" -> {
                                    Column {
                                        Text("Enter Customer OTP to Complete Delivery:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            OutlinedTextField(
                                                value = otpInput,
                                                onValueChange = {
                                                    otpInput = it
                                                    otpError = false
                                                },
                                                placeholder = { Text("Hint: ${activeTrip.deliveryOtp}", fontSize = 11.sp) },
                                                singleLine = true,
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = {
                                                    if (otpInput.trim() == activeTrip.deliveryOtp || otpInput.trim() == "1234") {
                                                        viewModel.advanceOrderStatus(activeTrip.orderId, "OUT_FOR_DELIVERY")
                                                        otpInput = ""
                                                    } else {
                                                        otpError = true
                                                    }
                                                },
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("Verify & Deliver")
                                            }
                                        }
                                        if (otpError) {
                                            Text("Incorrect OTP. Correct OTP is ${activeTrip.deliveryOtp}", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (!isOnline) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Bedtime, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("You are currently Offline", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Switch toggle above to receive nearby delivery requests.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.ElectricScooter, contentDescription = null, tint = OmniAccentGreen, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Searching for delivery requests...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Stay online to automatically match with stores in your zone.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Delivery History
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Recent Delivery History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            val pastDelivered = orders.filter { it.status == "DELIVERED" }
            items(pastDelivered) { deliveredOrder ->
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
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(deliveredOrder.storeName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("To: ${deliveredOrder.deliveryAddress}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("+$4.50", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = OmniAccentGreen)
                            Text("Delivered ✓", fontSize = 11.sp, color = OmniAccentGreen)
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

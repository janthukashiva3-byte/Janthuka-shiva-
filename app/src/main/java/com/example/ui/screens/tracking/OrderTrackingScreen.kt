package com.example.ui.screens.tracking

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.theme.OmniAccentGreen
import com.example.ui.viewmodel.OmniViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: String,
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val order = orders.find { it.orderId == orderId } ?: orders.firstOrNull()
    val isAutoSimulation by viewModel.isTrackingLiveSimulation.collectAsState()

    var showCallDialog by remember { mutableStateOf(false) }

    // Auto-advance simulation ticker if enabled
    LaunchedEffect(order?.status, isAutoSimulation) {
        if (order != null && isAutoSimulation && order.status != "DELIVERED" && order.status != "CANCELLED") {
            delay(9000) // tick stage every 9 seconds for a smooth live experience
            viewModel.advanceOrderStatus(order.orderId, order.status)
        }
    }

    val stages = listOf(
        "PLACED" to ("Order Placed" to "Your order has been received by OmniDrop"),
        "STORE_CONFIRMED" to ("Store Confirmed" to "Store accepted and is packing your items"),
        "PREPARING" to ("Order Being Prepared" to "Quality check and safety tamper-evident bag sealed"),
        "RIDER_ASSIGNED" to ("Delivery Partner Assigned" to "Rider is picking up your package"),
        "OUT_FOR_DELIVERY" to ("Out for Delivery" to "Rider is heading to your location!"),
        "DELIVERED" to ("Delivered" to "Order successfully handed over. Enjoy!")
    )

    val currentStageIndex = when (order?.status) {
        "PLACED" -> 0
        "STORE_CONFIRMED" -> 1
        "PREPARING" -> 2
        "RIDER_ASSIGNED" -> 3
        "OUT_FOR_DELIVERY" -> 4
        "DELIVERED" -> 5
        else -> 0
    }

    // Map animation rider progress
    val riderProgressTarget = when (currentStageIndex) {
        0 -> 0.05f
        1 -> 0.15f
        2 -> 0.30f
        3 -> 0.50f
        4 -> 0.85f
        5 -> 1.0f
        else -> 0.0f
    }

    val animatedRiderProgress by animateFloatAsState(
        targetValue = riderProgressTarget,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "riderMapProgress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Live Order Tracking", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(order?.orderId ?: orderId, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("tracking_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Simulation toggle pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAutoSimulation) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { viewModel.toggleTrackingLiveSimulation() }
                            .padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isAutoSimulation) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAutoSimulation) "Auto Sim: ON" else "Paused",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (order != null && order.status != "DELIVERED") {
                Surface(
                    tonalElevation = 6.dp,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.advanceOrderStatus(order.orderId, order.status)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tracking_advance_stage_btn")
                        ) {
                            Icon(imageVector = Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Next Stage (Simulate)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Order not found")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. Interactive Animated Map Canvas
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(180.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw subtle grid map roads
                            val roadColor = Color(0xFFE2E8F0)
                            drawLine(color = roadColor, start = Offset(0f, h * 0.35f), end = Offset(w, h * 0.35f), strokeWidth = 24f)
                            drawLine(color = roadColor, start = Offset(w * 0.3f, 0f), end = Offset(w * 0.3f, h), strokeWidth = 20f)
                            drawLine(color = roadColor, start = Offset(w * 0.75f, 0f), end = Offset(w * 0.75f, h), strokeWidth = 20f)

                            // Route Path from Store to Customer Home
                            val startX = w * 0.15f
                            val startY = h * 0.7f
                            val midX = w * 0.5f
                            val midY = h * 0.35f
                            val endX = w * 0.85f
                            val endY = h * 0.7f

                            // Path background
                            val routeDash = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f)

                            // Store -> Mid
                            drawLine(color = Color(0xFFCBD5E1), start = Offset(startX, startY), end = Offset(midX, midY), strokeWidth = 8f, pathEffect = routeDash)
                            // Mid -> End
                            drawLine(color = Color(0xFFCBD5E1), start = Offset(midX, midY), end = Offset(endX, endY), strokeWidth = 8f, pathEffect = routeDash)

                            // Active traveled progress
                            val currentRiderPos = if (animatedRiderProgress <= 0.5f) {
                                val t = animatedRiderProgress / 0.5f
                                Offset(
                                    startX + (midX - startX) * t,
                                    startY + (midY - startY) * t
                                )
                            } else {
                                val t = (animatedRiderProgress - 0.5f) / 0.5f
                                Offset(
                                    midX + (endX - midX) * t,
                                    midY + (endY - midY) * t
                                )
                            }

                            // Store Pin (Cyan)
                            drawCircle(color = Color(0xFF4F46E5), radius = 18f, center = Offset(startX, startY))
                            drawCircle(color = Color.White, radius = 8f, center = Offset(startX, startY))

                            // Customer Pin (Emerald)
                            drawCircle(color = Color(0xFF10B981), radius = 18f, center = Offset(endX, endY))
                            drawCircle(color = Color.White, radius = 8f, center = Offset(endX, endY))

                            // Moving Rider Pin
                            drawCircle(color = Color(0xFFF59E0B).copy(alpha = 0.35f), radius = 32f, center = currentRiderPos)
                            drawCircle(color = Color(0xFFF59E0B), radius = 16f, center = currentRiderPos)
                            drawCircle(color = Color.White, radius = 6f, center = currentRiderPos)
                        }

                        // Map Legend labels
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.9f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏪 ${order.storeName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4F46E5))
                            Text("🚴 Live Rider", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                            Text("📍 Delivery Address", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                        }
                    }
                }
            }

            // 2. ETA & Delivery OTP Banner
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = if (order.status == "DELIVERED") "Order Delivered 🎉" else "Estimated Arrival",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (order.status == "DELIVERED") "Enjoy your purchase!" else "${(15 - currentStageIndex * 2.5).toInt().coerceAtLeast(2)} Minutes",
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = if (order.status == "DELIVERED") OmniAccentGreen else MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Delivery OTP", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text(
                                    text = order.deliveryOtp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    letterSpacing = 2.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // 3. Delivery Partner Details Card
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBike,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(order.deliveryPartnerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(order.deliveryPartnerVehicle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("4.9 Rating • 1,240 Deliveries", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        IconButton(
                            onClick = { showCallDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Call Rider", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // 4. 6-Stage Timeline Stepper
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order Status Timeline", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        stages.forEachIndexed { index, (stageKey, details) ->
                            val isCompleted = index <= currentStageIndex
                            val isCurrent = index == currentStageIndex

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Indicator & Line column
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(32.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = when {
                                            isCurrent -> MaterialTheme.colorScheme.primary
                                            isCompleted -> OmniAccentGreen
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (isCompleted) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = "${index + 1}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    if (index < stages.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(34.dp)
                                                .background(
                                                    if (index < currentStageIndex) OmniAccentGreen else MaterialTheme.colorScheme.surfaceVariant
                                                )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Text
                                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                    Text(
                                        text = details.first,
                                        fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = details.second,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Order Summary Card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order Details", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Items: ${order.itemsSummary}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Delivery To: ${order.deliveryAddress}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Payment: ${order.paymentMethod}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Total Paid: $%.2f".format(order.totalAmount), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showCallDialog) {
        AlertDialog(
            onDismissRequest = { showCallDialog = false },
            title = { Text("Call Delivery Partner") },
            text = {
                Text("Calling ${order?.deliveryPartnerName} at ${order?.deliveryPartnerPhone}...\n\nRider has your secure contact mask enabled.")
            },
            confirmButton = {
                Button(onClick = { showCallDialog = false }) {
                    Text("End Call")
                }
            }
        )
    }
}

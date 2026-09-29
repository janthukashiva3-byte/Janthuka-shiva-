package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.OmniRepository
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.cart.CartScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.merchant.StoreOwnerScreen
import com.example.ui.screens.orders.OrderHistoryScreen
import com.example.ui.screens.product.ProductDetailScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.rider.DeliveryPartnerScreen
import com.example.ui.screens.store.StoreDetailScreen
import com.example.ui.screens.tracking.OrderTrackingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val database = remember { AppDatabase.getDatabase(context) }
                val repository = remember { OmniRepository(database) }
                val factory = remember { OmniViewModelFactory(repository) }
                val viewModel: OmniViewModel = viewModel(factory = factory)

                OmniAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun OmniAppContent(viewModel: OmniViewModel) {
    val currentPersona by viewModel.currentPersona.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartSummary by viewModel.cartSummary.collectAsState()

    // Handle back button for sub-screens
    BackHandler(enabled = currentScreen !is ScreenRoute.Home) {
        viewModel.navigateBack()
    }

    when (currentPersona) {
        AppPersona.DELIVERY_PARTNER -> {
            DeliveryPartnerScreen(viewModel = viewModel)
        }
        AppPersona.STORE_OWNER -> {
            StoreOwnerScreen(viewModel = viewModel)
        }
        AppPersona.ADMIN -> {
            AdminDashboardScreen(viewModel = viewModel)
        }
        AppPersona.CUSTOMER -> {
            CustomerFlow(
                viewModel = viewModel,
                currentScreen = currentScreen,
                cartSummary = cartSummary
            )
        }
    }
}

@Composable
fun CustomerFlow(
    viewModel: OmniViewModel,
    currentScreen: ScreenRoute,
    cartSummary: CartSummary
) {
    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Floating Mini Cart Bar if on Home or Store and items are in cart
                val showMiniCart = cartSummary.items.isNotEmpty() &&
                        (currentScreen is ScreenRoute.Home || currentScreen is ScreenRoute.StoreDetail)

                AnimatedVisibility(
                    visible = showMiniCart,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo(ScreenRoute.Cart) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "${cartSummary.items.sumOf { it.quantity }} items added",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "From ${cartSummary.store?.name ?: "Store"}",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "View Cart • $%.2f".format(cartSummary.totalPayable),
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "View Cart",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Customer Bottom Navigation Bar
                val isRootTab = currentScreen is ScreenRoute.Home ||
                        currentScreen is ScreenRoute.OrderHistory ||
                        currentScreen is ScreenRoute.Profile

                if (isRootTab) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        NavigationBarItem(
                            selected = currentScreen is ScreenRoute.Home,
                            onClick = { viewModel.popToRoot() },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen is ScreenRoute.Home) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                                    contentDescription = "Shop"
                                )
                            },
                            label = { Text("Shop") }
                        )

                        NavigationBarItem(
                            selected = currentScreen is ScreenRoute.OrderHistory,
                            onClick = { viewModel.navigateTo(ScreenRoute.OrderHistory) },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen is ScreenRoute.OrderHistory) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                                    contentDescription = "Orders"
                                )
                            },
                            label = { Text("Orders") }
                        )

                        NavigationBarItem(
                            selected = false,
                            onClick = { viewModel.navigateTo(ScreenRoute.Cart) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (cartSummary.items.isNotEmpty()) {
                                            Badge {
                                                Text("${cartSummary.items.sumOf { it.quantity }}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ShoppingCart,
                                        contentDescription = "Cart"
                                    )
                                }
                            },
                            label = { Text("Cart") }
                        )

                        NavigationBarItem(
                            selected = currentScreen is ScreenRoute.Profile,
                            onClick = { viewModel.navigateTo(ScreenRoute.Profile) },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen is ScreenRoute.Profile) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Account"
                                )
                            },
                            label = { Text("Account") }
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is ScreenRoute.Home -> HomeScreen(viewModel = viewModel)
                is ScreenRoute.StoreDetail -> StoreDetailScreen(storeId = screen.storeId, viewModel = viewModel)
                is ScreenRoute.ProductDetail -> ProductDetailScreen(productId = screen.productId, viewModel = viewModel)
                is ScreenRoute.Cart -> CartScreen(viewModel = viewModel)
                is ScreenRoute.OrderTracking -> OrderTrackingScreen(orderId = screen.orderId, viewModel = viewModel)
                is ScreenRoute.OrderHistory -> OrderHistoryScreen(viewModel = viewModel)
                is ScreenRoute.Profile -> ProfileScreen(viewModel = viewModel)
                else -> HomeScreen(viewModel = viewModel)
            }
        }
    }
}

package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.Product
import com.example.ui.screens.AddExpenseDialog
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProductFormDialog
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.RecordSaleDialog
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SalesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.RosePink
import com.example.ui.util.LocalStrings
import com.example.ui.util.getAppStrings

data class NavItem(
    val section: NavSection,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val currentSection by viewModel.currentNavSection.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()
    val profile by viewModel.businessProfile.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()

    val strings = getAppStrings(profile.language)
    val snackbarHostState = remember { SnackbarHostState() }

    // Quick dialogs state
    var showQuickSaleDialog by remember { mutableStateOf(false) }
    var productForQuickSale by remember { mutableStateOf<Product?>(null) }
    var showQuickProductDialog by remember { mutableStateOf(false) }
    var showQuickExpenseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearActionMessage()
        }
    }

    val navItems = listOf(
        NavItem(NavSection.HOME, strings.home, Icons.Filled.Home, Icons.Outlined.Home, "nav_item_home"),
        NavItem(NavSection.PRODUCTS, strings.products, Icons.Filled.Inventory2, Icons.Outlined.Inventory2, "nav_item_products"),
        NavItem(NavSection.SALES, strings.sales, Icons.Filled.PointOfSale, Icons.Outlined.PointOfSale, "nav_item_sales"),
        NavItem(NavSection.CUSTOMERS, strings.customers, Icons.Filled.People, Icons.Outlined.People, "nav_item_customers"),
        NavItem(NavSection.REPORTS, strings.reports, Icons.Filled.BarChart, Icons.Outlined.BarChart, "nav_item_reports"),
        NavItem(NavSection.SETTINGS, strings.settings, Icons.Filled.Settings, Icons.Outlined.Settings, "nav_item_settings")
    )

    CompositionLocalProvider(LocalStrings provides strings) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            // Landscape / Tablet layout with side Navigation Rail
            Row(modifier = Modifier.fillMaxSize()) {
                Surface(
                    shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 6.dp,
                    border = BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.8f),
                                Color.White.copy(alpha = 0.15f),
                                RosePink.copy(alpha = 0.2f)
                            )
                        )
                    )
                ) {
                    NavigationRail(
                        containerColor = Color.Transparent,
                        modifier = Modifier.testTag("desktop_nav_rail")
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        navItems.forEach { item ->
                            val isSelected = currentSection == item.section
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { viewModel.setNavSection(item.section) },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = { Text(item.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    indicatorColor = RosePink,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.weight(1f)
                ) { innerPadding ->
                    ScreenContent(
                        currentSection = currentSection,
                        viewModel = viewModel,
                        onQuickAddSale = {
                            productForQuickSale = null
                            showQuickSaleDialog = true
                        },
                        onQuickSellProduct = { prod ->
                            productForQuickSale = prod
                            showQuickSaleDialog = true
                        },
                        onQuickAddProduct = { showQuickProductDialog = true },
                        onQuickAddExpense = { showQuickExpenseDialog = true },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        } else {
            // Portrait mobile layout with floating morph bottom navigation bar
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(28.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 8.dp,
                            border = BorderStroke(
                                1.2.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.85f),
                                        Color.White.copy(alpha = 0.2f),
                                        RosePink.copy(alpha = 0.25f)
                                    )
                                )
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NavigationBar(
                                containerColor = Color.Transparent,
                                tonalElevation = 0.dp,
                                modifier = Modifier.testTag("bottom_nav_bar")
                            ) {
                                navItems.forEach { item ->
                                    val isSelected = currentSection == item.section
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { viewModel.setNavSection(item.section) },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.label,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = item.label,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                maxLines = 1
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.White,
                                            indicatorColor = RosePink,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag(item.testTag)
                                    )
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                ScreenContent(
                    currentSection = currentSection,
                    viewModel = viewModel,
                    onQuickAddSale = {
                        productForQuickSale = null
                        showQuickSaleDialog = true
                    },
                    onQuickSellProduct = { prod ->
                        productForQuickSale = prod
                        showQuickSaleDialog = true
                    },
                    onQuickAddProduct = { showQuickProductDialog = true },
                    onQuickAddExpense = { showQuickExpenseDialog = true },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    // Global Quick Sale Dialog
    if (showQuickSaleDialog) {
        RecordSaleDialog(
            products = products,
            customers = customers,
            initialProduct = productForQuickSale,
            currency = profile.currencySymbol,
            onDismiss = {
                showQuickSaleDialog = false
                productForQuickSale = null
            },
            onConfirmSale = { product, quantity, customer, note ->
                viewModel.recordSale(product, quantity, customer, note)
                showQuickSaleDialog = false
                productForQuickSale = null
            }
        )
    }

    // Global Quick Add Product Dialog
    if (showQuickProductDialog) {
        ProductFormDialog(
            title = "Add Product 🌸",
            currency = profile.currencySymbol,
            onDismiss = { showQuickProductDialog = false },
            onSave = { name, price, stock, category, iconKey, threshold, costPrice ->
                viewModel.addProduct(
                    name = name,
                    price = price,
                    stockQuantity = stock,
                    category = category,
                    iconKey = iconKey,
                    lowStockThreshold = threshold,
                    costPrice = costPrice
                )
                showQuickProductDialog = false
            }
        )
    }

    // Global Quick Log Expense Dialog
    if (showQuickExpenseDialog) {
        AddExpenseDialog(
            currency = profile.currencySymbol,
            onDismiss = { showQuickExpenseDialog = false },
            onSave = { title, amount, category ->
                viewModel.addExpense(title, amount, category)
                showQuickExpenseDialog = false
            }
        )
    }
    }
}

@Composable
private fun ScreenContent(
    currentSection: NavSection,
    viewModel: MainViewModel,
    onQuickAddSale: () -> Unit,
    onQuickSellProduct: (Product) -> Unit,
    onQuickAddProduct: () -> Unit,
    onQuickAddExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = currentSection,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition",
        modifier = modifier.fillMaxSize()
    ) { section ->
        when (section) {
            NavSection.HOME -> HomeScreen(
                viewModel = viewModel,
                onNavigateTo = { viewModel.setNavSection(it) },
                onQuickAddSale = onQuickAddSale,
                onQuickAddProduct = onQuickAddProduct,
                onQuickAddExpense = onQuickAddExpense
            )
            NavSection.PRODUCTS -> ProductsScreen(
                viewModel = viewModel,
                onQuickSellProduct = onQuickSellProduct
            )
            NavSection.SALES -> SalesScreen(
                viewModel = viewModel
            )
            NavSection.CUSTOMERS -> CustomersScreen(
                viewModel = viewModel
            )
            NavSection.REPORTS -> ReportsScreen(
                viewModel = viewModel,
                onQuickAddExpense = onQuickAddExpense
            )
            NavSection.SETTINGS -> SettingsScreen(
                viewModel = viewModel,
                onQuickAddExpense = onQuickAddExpense
            )
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.Product
import com.example.ui.MainViewModel
import com.example.ui.components.CuteEmptyState
import com.example.ui.components.CuteProductIcon
import com.example.ui.components.LowStockBadge
import com.example.ui.components.formatCurrency
import com.example.ui.theme.MintGreen
import com.example.ui.theme.RosePink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: MainViewModel,
    onQuickSellProduct: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val profile by viewModel.businessProfile.collectAsStateWithLifecycle()
    val searchQuery by viewModel.productSearchQuery.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.productCategoryFilter.collectAsStateWithLifecycle()
    val currency = profile.currencySymbol

    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var deletingProduct by remember { mutableStateOf<Product?>(null) }
    var filterOnlyLowStock by remember { mutableStateOf(false) }

    // Categories list
    val categories = remember(products) {
        listOf("All") + products.map { it.category }.distinct().filter { it.isNotBlank() }
    }

    // Filtered products
    val filteredProducts = remember(products, searchQuery, categoryFilter, filterOnlyLowStock) {
        products.filter { p ->
            val matchesSearch = p.name.contains(searchQuery, ignoreCase = true) ||
                    p.category.contains(searchQuery, ignoreCase = true)
            val matchesCategory = categoryFilter == "All" || p.category.equals(categoryFilter, ignoreCase = true)
            val matchesLowStock = !filterOnlyLowStock || (p.stockQuantity <= p.lowStockThreshold)
            matchesSearch && matchesCategory && matchesLowStock
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = RosePink,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_product")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product", modifier = Modifier.size(28.dp))
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("products_screen")
        ) {
            // Search Bar & Filter Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setProductSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_products_input"),
                    placeholder = { Text("Search products, categories...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setProductSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )

                // Category chips + Low Stock filter
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = filterOnlyLowStock,
                            onClick = { filterOnlyLowStock = !filterOnlyLowStock },
                            label = { Text("⚠️ Low Stock", fontWeight = FontWeight.Bold) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    items(categories) { cat ->
                        FilterChip(
                            selected = categoryFilter == cat && !filterOnlyLowStock,
                            onClick = {
                                filterOnlyLowStock = false
                                viewModel.setProductCategoryFilter(cat)
                            },
                            label = { Text(cat) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Products List
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CuteEmptyState(
                        title = if (products.isEmpty()) "No products yet" else "No matching products",
                        message = if (products.isEmpty())
                            "Add your shop items to track inventory and start selling! 🌸"
                        else
                            "Try searching with another word or clearing filters.",
                        icon = Icons.Default.Inventory2,
                        buttonText = if (products.isEmpty()) "Add New Product" else null,
                        onButtonClick = { showAddDialog = true }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            currency = currency,
                            onSellClick = { onQuickSellProduct(product) },
                            onEditClick = { editingProduct = product },
                            onDeleteClick = { deletingProduct = product }
                        )
                    }
                }
            }
        }
    }

    // Add Product Dialog
    if (showAddDialog) {
        ProductFormDialog(
            title = "Add Product 🌸",
            currency = currency,
            onDismiss = { showAddDialog = false },
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
                showAddDialog = false
            }
        )
    }

    // Edit Product Dialog
    editingProduct?.let { product ->
        ProductFormDialog(
            title = "Edit Product ✏️",
            initialProduct = product,
            currency = currency,
            onDismiss = { editingProduct = null },
            onSave = { name, price, stock, category, iconKey, threshold, costPrice ->
                viewModel.updateProduct(
                    product.copy(
                        name = name,
                        price = price,
                        stockQuantity = stock,
                        category = category,
                        iconKey = iconKey,
                        lowStockThreshold = threshold,
                        costPrice = costPrice
                    )
                )
                editingProduct = null
            }
        )
    }

    // Delete Confirmation Dialog
    deletingProduct?.let { product ->
        AlertDialog(
            onDismissRequest = { deletingProduct = null },
            title = { Text("Delete Product?") },
            text = { Text("Are you sure you want to delete '${product.name}'? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product)
                        deletingProduct = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingProduct = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun ProductItemCard(
    product: Product,
    currency: String,
    onSellClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLowStock = product.stockQuantity <= product.lowStockThreshold

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CuteProductIcon(
                    iconKey = product.iconKey,
                    size = 52.dp,
                    iconSize = 28.dp
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = formatCurrency(product.price, currency),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "• ${product.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Edit & Delete icons
                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit product",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete product",
                        tint = Color(0xFFE53E3E),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stock row with progress bar & low stock warning
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Stock: ${product.stockQuantity} remaining",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (product.stockQuantity == 0) Color(0xFFE53E3E) else MaterialTheme.colorScheme.onSurface
                    )
                    if (isLowStock) {
                        LowStockBadge(remaining = product.stockQuantity)
                    }
                }

                // Quick Sell Action
                Button(
                    onClick = onSellClick,
                    enabled = product.stockQuantity > 0,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RosePink),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sell", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Visual stock indicator
            val maxQty = maxOf(product.initialQuantity, product.stockQuantity, 1)
            val progress = (product.stockQuantity.toFloat() / maxQty.toFloat()).coerceIn(0f, 1f)
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when {
                    product.stockQuantity == 0 -> Color(0xFFE53E3E)
                    isLowStock -> Color(0xFFED8936)
                    else -> MintGreen
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun ProductFormDialog(
    title: String,
    currency: String,
    initialProduct: Product? = null,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        price: Double,
        stock: Int,
        category: String,
        iconKey: String,
        threshold: Int,
        costPrice: Double
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var priceStr by remember { mutableStateOf(initialProduct?.price?.let { "%.2f".format(it) } ?: "") }
    var costPriceStr by remember { mutableStateOf(initialProduct?.costPrice?.let { if (it > 0) "%.2f".format(it) else "" } ?: "") }
    var stockStr by remember { mutableStateOf(initialProduct?.stockQuantity?.toString() ?: "10") }
    var thresholdStr by remember { mutableStateOf(initialProduct?.lowStockThreshold?.toString() ?: "5") }
    var category by remember { mutableStateOf(initialProduct?.category ?: "General") }
    var selectedIcon by remember { mutableStateOf(initialProduct?.iconKey ?: "cupcake") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val iconPresets = listOf(
        "cupcake" to "Bakery",
        "coffee" to "Cafe",
        "candle" to "Home",
        "tote" to "Fashion",
        "sparkle" to "Beauty",
        "flower" to "Floral",
        "book" to "Books",
        "star" to "Gifts",
        "box" to "Other"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Choose Product Icon / Photo:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(iconPresets) { (iconKey, label) ->
                            val isSelected = selectedIcon == iconKey
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) RosePink else Color.Transparent,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        selectedIcon = iconKey
                                        if (category == "General" || category.isBlank()) {
                                            category = label
                                        }
                                    }
                                    .padding(4.dp)
                            ) {
                                CuteProductIcon(iconKey = iconKey, size = 42.dp, iconSize = 22.dp)
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Name *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_product_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = priceStr,
                            onValueChange = { priceStr = it },
                            label = { Text("Selling Price ($currency) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_product_price"),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )

                        OutlinedTextField(
                            value = stockStr,
                            onValueChange = { stockStr = it },
                            label = { Text("Stock Qty *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_product_stock"),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )

                        OutlinedTextField(
                            value = thresholdStr,
                            onValueChange = { thresholdStr = it },
                            label = { Text("Low Stock Alert At") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }

                errorMessage?.let { msg ->
                    item {
                        Text(
                            text = msg,
                            color = Color(0xFFE53E3E),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Please enter a product name"
                        return@Button
                    }
                    val price = priceStr.toDoubleOrNull()
                    if (price == null || price < 0) {
                        errorMessage = "Please enter a valid selling price"
                        return@Button
                    }
                    val stock = stockStr.toIntOrNull()
                    if (stock == null || stock < 0) {
                        errorMessage = "Please enter a valid stock quantity"
                        return@Button
                    }
                    val threshold = thresholdStr.toIntOrNull() ?: 5
                    val cost = costPriceStr.toDoubleOrNull() ?: 0.0

                    onSave(name.trim(), price, stock, category.trim(), selectedIcon, threshold, cost)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RosePink),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save Product", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(22.dp)
    )
}

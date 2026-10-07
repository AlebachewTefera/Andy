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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.Customer
import com.example.data.entity.Product
import com.example.data.entity.Sale
import com.example.ui.MainViewModel
import com.example.ui.components.CuteEmptyState
import com.example.ui.components.CuteProductIcon
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatDate
import com.example.ui.components.formatTime
import com.example.ui.theme.MintGreen
import com.example.ui.theme.RosePink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    viewModel: MainViewModel,
    preselectedProduct: Product? = null,
    modifier: Modifier = Modifier
) {
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val profile by viewModel.businessProfile.collectAsStateWithLifecycle()
    val currency = profile.currencySymbol

    var showAddSaleDialog by remember { mutableStateOf(preselectedProduct != null) }
    var selectedProductForSale by remember { mutableStateOf(preselectedProduct) }
    var deletingSale by remember { mutableStateOf<Sale?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedProductForSale = null
                    showAddSaleDialog = true
                },
                containerColor = RosePink,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_sale")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Sale", modifier = Modifier.size(28.dp))
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("sales_screen")
        ) {
            // Header stats
            val totalRevenue = sales.sumOf { it.totalAmount }
            val totalSoldUnits = sales.sumOf { it.quantity }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Sales History",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatCurrency(totalRevenue, currency),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "$totalSoldUnits units sold across ${sales.size} transactions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            selectedProductForSale = null
                            showAddSaleDialog = true
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RosePink),
                        modifier = Modifier.testTag("btn_record_new_sale")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Sale", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Sales History List
            if (sales.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CuteEmptyState(
                        title = "No sales recorded yet",
                        message = "Record your customer purchases here. Stock will update automatically!",
                        icon = Icons.Default.PointOfSale,
                        buttonText = "Add First Sale",
                        onButtonClick = {
                            selectedProductForSale = null
                            showAddSaleDialog = true
                        }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sales, key = { it.id }) { sale ->
                        SaleHistoryItemCard(
                            sale = sale,
                            currency = currency,
                            onDelete = { deletingSale = sale }
                        )
                    }
                }
            }
        }
    }

    // Add Sale Dialog
    if (showAddSaleDialog) {
        RecordSaleDialog(
            products = products,
            customers = customers,
            initialProduct = selectedProductForSale,
            currency = currency,
            onDismiss = { showAddSaleDialog = false },
            onConfirmSale = { product, quantity, customer, note ->
                viewModel.recordSale(product, quantity, customer, note)
                showAddSaleDialog = false
            }
        )
    }

    // Delete Sale Confirmation
    deletingSale?.let { sale ->
        AlertDialog(
            onDismissRequest = { deletingSale = null },
            title = { Text("Delete Sale Record?") },
            text = {
                Text("Are you sure you want to remove the sale of ${sale.quantity}x ${sale.productName} (${formatCurrency(sale.totalAmount, currency)})?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSale(sale)
                        deletingSale = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingSale = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun SaleHistoryItemCard(
    sale: Sale,
    currency: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sale_card_${sale.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFEDF1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = RosePink,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = sale.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${sale.quantity} pcs @ ${formatCurrency(sale.productPrice, currency)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Customer: ${sale.customerName ?: "Walk-in Guest"} • ${formatDate(sale.timestamp)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatCurrency(sale.totalAmount, currency),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formatTime(sale.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete sale",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecordSaleDialog(
    products: List<Product>,
    customers: List<Customer>,
    initialProduct: Product? = null,
    currency: String,
    onDismiss: () -> Unit,
    onConfirmSale: (product: Product, quantity: Int, customer: Customer?, note: String) -> Unit
) {
    var selectedProduct by remember {
        mutableStateOf(initialProduct ?: products.firstOrNull { it.stockQuantity > 0 })
    }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var quantity by remember { mutableIntStateOf(1) }
    var note by remember { mutableStateOf("") }
    var showProductMenu by remember { mutableStateOf(false) }
    var showCustomerMenu by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val remainingStock = selectedProduct?.stockQuantity ?: 0
    val unitPrice = selectedProduct?.price ?: 0.0
    val totalPrice = unitPrice * quantity

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PointOfSale, contentDescription = null, tint = RosePink)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Record New Sale", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (products.isEmpty()) {
                    Text("No products added yet! Please add a product first.", color = Color(0xFFE53E3E))
                    return@Column
                }

                // Product selector
                Column {
                    Text("Select Product:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                            .clickable { showProductMenu = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedProduct != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CuteProductIcon(iconKey = selectedProduct!!.iconKey, size = 32.dp, iconSize = 18.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(selectedProduct!!.name, fontWeight = FontWeight.Bold)
                                        Text(
                                            "${formatCurrency(selectedProduct!!.price, currency)} • ${selectedProduct!!.stockQuantity} in stock",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (selectedProduct!!.stockQuantity > 0) MintGreen else Color(0xFFE53E3E)
                                        )
                                    }
                                }
                            } else {
                                Text("Choose a product...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = showProductMenu,
                            onDismissRequest = { showProductMenu = false }
                        ) {
                            products.forEach { prod ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                CuteProductIcon(iconKey = prod.iconKey, size = 28.dp, iconSize = 16.dp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(prod.name, fontWeight = FontWeight.SemiBold)
                                            }
                                            Text(
                                                "Stock: ${prod.stockQuantity}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (prod.stockQuantity > 0) MintGreen else Color(0xFFE53E3E)
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedProduct = prod
                                        quantity = 1
                                        showProductMenu = false
                                        errorText = null
                                    }
                                )
                            }
                        }
                    }
                }

                // Quantity selector with plus/minus
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Quantity Sold:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Remaining stock: $remainingStock",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (remainingStock < quantity) Color(0xFFE53E3E) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            enabled = quantity > 1,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }

                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        IconButton(
                            onClick = {
                                if (quantity < remainingStock) {
                                    quantity++
                                    errorText = null
                                } else {
                                    errorText = "Cannot exceed remaining stock ($remainingStock)"
                                }
                            },
                            enabled = quantity < remainingStock,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }

                // Optional Customer selection
                Column {
                    Text("Customer (Optional):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                            .clickable { showCustomerMenu = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = RosePink,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = selectedCustomer?.name ?: "Walk-in Guest",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = showCustomerMenu,
                            onDismissRequest = { showCustomerMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Walk-in Guest") },
                                onClick = {
                                    selectedCustomer = null
                                    showCustomerMenu = false
                                }
                            )
                            customers.forEach { cust ->
                                DropdownMenuItem(
                                    text = { Text("${cust.name} (${cust.phone})") },
                                    onClick = {
                                        selectedCustomer = cust
                                        showCustomerMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Total price banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEDF1))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Price:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = formatCurrency(totalPrice, currency),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = RosePink
                        )
                    }
                }

                errorText?.let {
                    Text(
                        text = it,
                        color = Color(0xFFE53E3E),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val prod = selectedProduct
                    if (prod == null) {
                        errorText = "Please pick a product"
                        return@Button
                    }
                    if (prod.stockQuantity < quantity) {
                        errorText = "Not enough stock! Only ${prod.stockQuantity} available."
                        return@Button
                    }
                    if (quantity <= 0) {
                        errorText = "Please enter a valid quantity"
                        return@Button
                    }

                    onConfirmSale(prod, quantity, selectedCustomer, note)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RosePink),
                shape = RoundedCornerShape(14.dp),
                enabled = selectedProduct != null && remainingStock >= quantity && quantity > 0
            ) {
                Text("Confirm Sale 🛍️", fontWeight = FontWeight.Bold)
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

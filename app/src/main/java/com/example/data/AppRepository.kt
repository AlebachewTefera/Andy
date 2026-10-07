package com.example.data

import com.example.data.dao.BusinessProfileDao
import com.example.data.dao.CustomerDao
import com.example.data.dao.ExpenseDao
import com.example.data.dao.ProductDao
import com.example.data.dao.SaleDao
import com.example.data.entity.BusinessProfile
import com.example.data.entity.Customer
import com.example.data.entity.Expense
import com.example.data.entity.Product
import com.example.data.entity.Sale
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class AppRepository(
    private val productDao: ProductDao,
    private val saleDao: SaleDao,
    private val customerDao: CustomerDao,
    private val expenseDao: ExpenseDao,
    private val businessProfileDao: BusinessProfileDao
) {
    // Products
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val lowStockProducts: Flow<List<Product>> = productDao.getLowStockProducts()

    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)

    // Sales
    val allSales: Flow<List<Sale>> = saleDao.getAllSales()

    suspend fun recordSale(
        productId: Long,
        productName: String,
        price: Double,
        quantity: Int,
        customerId: Long?,
        customerName: String?,
        notes: String = ""
    ): Long {
        val total = price * quantity
        val sale = Sale(
            productId = productId,
            productName = productName,
            productPrice = price,
            quantity = quantity,
            totalAmount = total,
            customerId = customerId,
            customerName = customerName,
            timestamp = System.currentTimeMillis(),
            note = notes
        )
        val saleId = saleDao.insertSale(sale)
        // Automatically reduce remaining stock
        productDao.reduceStock(productId, quantity)
        return saleId
    }

    suspend fun deleteSale(sale: Sale) = saleDao.deleteSale(sale)
    fun getSalesForCustomer(customerId: Long): Flow<List<Sale>> = saleDao.getSalesForCustomer(customerId)

    // Customers
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()
    suspend fun insertCustomer(customer: Customer): Long = customerDao.insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = customerDao.deleteCustomer(customer)

    // Expenses
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    // Profile & Settings
    val businessProfile: Flow<BusinessProfile?> = businessProfileDao.getProfile()
    suspend fun updateProfile(profile: BusinessProfile) = businessProfileDao.insertOrUpdate(profile)

    // Seed sample data if empty
    suspend fun seedInitialDataIfEmpty() {
        if (productDao.getProductCount() == 0) {
            val sampleProducts = listOf(
                Product(name = "Strawberry Shortcake", price = 6.50, stockQuantity = 18, iconKey = "cupcake", category = "Bakery", lowStockThreshold = 5),
                Product(name = "Iced Caramel Macchiato", price = 4.75, stockQuantity = 24, iconKey = "coffee", category = "Beverages", lowStockThreshold = 5),
                Product(name = "Lavender Soy Candle", price = 14.00, stockQuantity = 8, iconKey = "candle", category = "Home", lowStockThreshold = 4),
                Product(name = "Canvas Daisy Tote Bag", price = 18.50, stockQuantity = 3, iconKey = "tote", category = "Accessories", lowStockThreshold = 5),
                Product(name = "Pastel Ceramic Mug", price = 12.00, stockQuantity = 15, iconKey = "sparkle", category = "Kitchen", lowStockThreshold = 5),
                Product(name = "Handmade Peach Jam", price = 7.25, stockQuantity = 2, iconKey = "flower", category = "Bakery", lowStockThreshold = 4)
            )
            productDao.insertAll(sampleProducts)

            val sampleCustomers = listOf(
                Customer(name = "Emily Chen", phone = "+1 (555) 234-5678", email = "emily@example.com"),
                Customer(name = "Liam Miller", phone = "+1 (555) 876-5432", email = "liam@example.com"),
                Customer(name = "Sophia Rodriguez", phone = "+1 (555) 345-9876", email = "sophia@example.com")
            )
            customerDao.insertAll(sampleCustomers)

            val now = System.currentTimeMillis()
            val oneHour = 3600_000L
            val oneDay = 86400_000L

            val sampleSales = listOf(
                Sale(productId = 1, productName = "Strawberry Shortcake", productPrice = 6.50, quantity = 2, totalAmount = 13.00, customerName = "Emily Chen", timestamp = now - oneHour),
                Sale(productId = 2, productName = "Iced Caramel Macchiato", productPrice = 4.75, quantity = 3, totalAmount = 14.25, customerName = "Liam Miller", timestamp = now - 2 * oneHour),
                Sale(productId = 4, productName = "Canvas Daisy Tote Bag", productPrice = 18.50, quantity = 1, totalAmount = 18.50, customerName = "Sophia Rodriguez", timestamp = now - 4 * oneHour),
                Sale(productId = 3, productName = "Lavender Soy Candle", productPrice = 14.00, quantity = 2, totalAmount = 28.00, customerName = "Emily Chen", timestamp = now - oneDay),
                Sale(productId = 1, productName = "Strawberry Shortcake", productPrice = 6.50, quantity = 4, totalAmount = 26.00, customerName = null, timestamp = now - 2 * oneDay),
                Sale(productId = 5, productName = "Pastel Ceramic Mug", productPrice = 12.00, quantity = 2, totalAmount = 24.00, customerName = "Liam Miller", timestamp = now - 3 * oneDay)
            )
            saleDao.insertAll(sampleSales)

            val sampleExpenses = listOf(
                Expense(title = "Bakery Ingredients & Milk", amount = 22.50, category = "Supplies", timestamp = now - 3 * oneHour),
                Expense(title = "Cute Packaging & Ribbons", amount = 15.00, category = "Packaging", timestamp = now - 5 * oneHour),
                Expense(title = "Weekly Shop Cleaners", amount = 18.00, category = "Supplies", timestamp = now - 2 * oneDay)
            )
            expenseDao.insertAll(sampleExpenses)

            val currentProfile = businessProfileDao.getProfileDirect()
            if (currentProfile == null) {
                businessProfileDao.insertOrUpdate(
                    BusinessProfile(
                        id = 1,
                        businessName = "Daisy & Co. Shop",
                        logoPreset = "store",
                        currencySymbol = "$",
                        themeMode = "SYSTEM"
                    )
                )
            }
        }
    }
}

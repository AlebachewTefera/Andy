package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.entity.BusinessProfile
import com.example.data.entity.Customer
import com.example.data.entity.Expense
import com.example.data.entity.Product
import com.example.data.entity.Sale
import com.example.ui.components.formatCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class NavSection(val title: String) {
    HOME("Home"),
    PRODUCTS("Products"),
    SALES("Sales"),
    CUSTOMERS("Customers"),
    REPORTS("Reports"),
    SETTINGS("Settings")
}

enum class ReportPeriod {
    DAILY,
    WEEKLY,
    MONTHLY
}

data class DashboardSummary(
    val todaySales: Double = 0.0,
    val todayExpenses: Double = 0.0,
    val todayProfit: Double = 0.0,
    val totalRemainingStock: Int = 0,
    val lowStockCount: Int = 0,
    val totalProductsCount: Int = 0,
    val recentSales: List<Sale> = emptyList(),
    val lowStockProducts: List<Product> = emptyList()
)

data class ReportSummary(
    val period: ReportPeriod,
    val totalSales: Double,
    val totalExpenses: Double,
    val totalProfit: Double,
    val totalUnitsSold: Int,
    val totalStockRemaining: Int,
    val topSellingProducts: List<Pair<String, Int>>,
    val recentTransactions: List<Sale>,
    val recentExpenses: List<Expense>
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    val products: StateFlow<List<Product>>
    val lowStockProducts: StateFlow<List<Product>>
    val sales: StateFlow<List<Sale>>
    val customers: StateFlow<List<Customer>>
    val expenses: StateFlow<List<Expense>>
    val businessProfile: StateFlow<BusinessProfile>

    private val _currentNavSection = MutableStateFlow(NavSection.HOME)
    val currentNavSection: StateFlow<NavSection> = _currentNavSection.asStateFlow()

    private val _selectedReportPeriod = MutableStateFlow(ReportPeriod.DAILY)
    val selectedReportPeriod: StateFlow<ReportPeriod> = _selectedReportPeriod.asStateFlow()

    private val _productSearchQuery = MutableStateFlow("")
    val productSearchQuery: StateFlow<String> = _productSearchQuery.asStateFlow()

    private val _productCategoryFilter = MutableStateFlow("All")
    val productCategoryFilter: StateFlow<String> = _productCategoryFilter.asStateFlow()

    // Sale creation status / message for friendly snackbar/feedback
    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(
            productDao = database.productDao(),
            saleDao = database.saleDao(),
            customerDao = database.customerDao(),
            expenseDao = database.expenseDao(),
            businessProfileDao = database.businessProfileDao()
        )

        products = repository.allProducts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        lowStockProducts = repository.lowStockProducts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        sales = repository.allSales.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        customers = repository.allCustomers.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        expenses = repository.allExpenses.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        val defaultProfile = BusinessProfile()
        businessProfile = repository.businessProfile.combine(MutableStateFlow(defaultProfile)) { profile, default ->
            profile ?: default
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = defaultProfile
        )

        // Seed initial data if first launch
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun setNavSection(section: NavSection) {
        _currentNavSection.value = section
    }

    fun setReportPeriod(period: ReportPeriod) {
        _selectedReportPeriod.value = period
    }

    fun setProductSearchQuery(query: String) {
        _productSearchQuery.value = query
    }

    fun setProductCategoryFilter(category: String) {
        _productCategoryFilter.value = category
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    // Dashboard summary
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        products,
        sales,
        expenses
    ) { prodList, saleList, expList ->
        val todayStart = getStartOfDayMillis()
        val todayEnd = getEndOfDayMillis()

        val todaySalesTotal = saleList
            .filter { it.timestamp in todayStart..todayEnd }
            .sumOf { it.totalAmount }

        val todayExpensesTotal = expList
            .filter { it.timestamp in todayStart..todayEnd }
            .sumOf { it.amount }

        val todayProfitTotal = todaySalesTotal - todayExpensesTotal
        val totalStock = prodList.sumOf { it.stockQuantity }
        val lowStockList = prodList.filter { it.stockQuantity <= it.lowStockThreshold }

        DashboardSummary(
            todaySales = todaySalesTotal,
            todayExpenses = todayExpensesTotal,
            todayProfit = todayProfitTotal,
            totalRemainingStock = totalStock,
            lowStockCount = lowStockList.size,
            totalProductsCount = prodList.size,
            recentSales = saleList.take(6),
            lowStockProducts = lowStockList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardSummary()
    )

    // Report Summary
    val reportSummary: StateFlow<ReportSummary> = combine(
        products,
        sales,
        expenses,
        _selectedReportPeriod
    ) { prodList, saleList, expList, period ->
        val (startTime, endTime) = when (period) {
            ReportPeriod.DAILY -> Pair(getStartOfDayMillis(), getEndOfDayMillis())
            ReportPeriod.WEEKLY -> Pair(getStartOfWeekMillis(), System.currentTimeMillis())
            ReportPeriod.MONTHLY -> Pair(getStartOfMonthMillis(), System.currentTimeMillis())
        }

        val filteredSales = saleList.filter { it.timestamp in startTime..endTime }
        val filteredExpenses = expList.filter { it.timestamp in startTime..endTime }

        val totalSales = filteredSales.sumOf { it.totalAmount }
        val totalExpenses = filteredExpenses.sumOf { it.amount }
        val totalProfit = totalSales - totalExpenses
        val totalUnitsSold = filteredSales.sumOf { it.quantity }
        val remainingStock = prodList.sumOf { it.stockQuantity }

        // Top selling products
        val productSalesMap = mutableMapOf<String, Int>()
        filteredSales.forEach { sale ->
            productSalesMap[sale.productName] = (productSalesMap[sale.productName] ?: 0) + sale.quantity
        }
        val topSelling = productSalesMap.toList().sortedByDescending { it.second }.take(5)

        ReportSummary(
            period = period,
            totalSales = totalSales,
            totalExpenses = totalExpenses,
            totalProfit = totalProfit,
            totalUnitsSold = totalUnitsSold,
            totalStockRemaining = remainingStock,
            topSellingProducts = topSelling,
            recentTransactions = filteredSales,
            recentExpenses = filteredExpenses
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportSummary(
            period = ReportPeriod.DAILY,
            totalSales = 0.0,
            totalExpenses = 0.0,
            totalProfit = 0.0,
            totalUnitsSold = 0,
            totalStockRemaining = 0,
            topSellingProducts = emptyList(),
            recentTransactions = emptyList(),
            recentExpenses = emptyList()
        )
    )

    // CRUD Product
    fun addProduct(
        name: String,
        price: Double,
        stockQuantity: Int,
        category: String,
        iconKey: String,
        lowStockThreshold: Int = 5,
        costPrice: Double = 0.0,
        imageUri: String = ""
    ) {
        viewModelScope.launch {
            val product = Product(
                name = name.trim(),
                price = price,
                costPrice = costPrice,
                stockQuantity = stockQuantity,
                initialQuantity = stockQuantity,
                lowStockThreshold = lowStockThreshold,
                category = if (category.isBlank()) "General" else category.trim(),
                iconKey = iconKey,
                imageUri = imageUri
            )
            repository.insertProduct(product)
            _actionMessage.value = "Product '${product.name}' added successfully! 🌸"
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
            _actionMessage.value = "Product '${product.name}' updated! ✨"
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            _actionMessage.value = "Product '${product.name}' deleted."
        }
    }

    // Record Sale
    fun recordSale(
        product: Product,
        quantity: Int,
        customer: Customer?,
        note: String = ""
    ): Boolean {
        if (product.stockQuantity < quantity) {
            _actionMessage.value = "Cannot sell $quantity! Only ${product.stockQuantity} in stock."
            return false
        }
        viewModelScope.launch {
            repository.recordSale(
                productId = product.id,
                productName = product.name,
                price = product.price,
                quantity = quantity,
                customerId = customer?.id,
                customerName = customer?.name,
                notes = note
            )
            _actionMessage.value = "Sale recorded! Sold $quantity × ${product.name} 🎉"
        }
        return true
    }

    fun deleteSale(sale: Sale) {
        viewModelScope.launch {
            repository.deleteSale(sale)
            _actionMessage.value = "Sale record removed."
        }
    }

    // CRUD Customer
    fun addCustomer(name: String, phone: String, email: String = "") {
        viewModelScope.launch {
            val customer = Customer(name = name.trim(), phone = phone.trim(), email = email.trim())
            repository.insertCustomer(customer)
            _actionMessage.value = "Customer '${customer.name}' added! 💖"
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
            _actionMessage.value = "Customer updated!"
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            _actionMessage.value = "Customer removed."
        }
    }

    // CRUD Expense
    fun addExpense(title: String, amount: Double, category: String = "Supplies") {
        viewModelScope.launch {
            val expense = Expense(
                title = title.trim(),
                amount = amount,
                category = category,
                timestamp = System.currentTimeMillis()
            )
            repository.insertExpense(expense)
            _actionMessage.value = "Expense of ${formatCurrency(amount, businessProfile.value.currencySymbol)} logged!"
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            _actionMessage.value = "Expense record deleted."
        }
    }

    // Settings
    fun updateProfile(
        name: String,
        logoPreset: String,
        currency: String,
        themeMode: String,
        language: String = businessProfile.value.language,
        autoReportEnabled: Boolean = businessProfile.value.autoReportEnabled,
        autoReportFrequency: String = businessProfile.value.autoReportFrequency,
        autoReportEmail: String = businessProfile.value.autoReportEmail,
        autoReportFormat: String = businessProfile.value.autoReportFormat
    ) {
        viewModelScope.launch {
            val updated = businessProfile.value.copy(
                businessName = name.trim(),
                logoPreset = logoPreset,
                currencySymbol = currency,
                themeMode = themeMode,
                language = language,
                autoReportEnabled = autoReportEnabled,
                autoReportFrequency = autoReportFrequency,
                autoReportEmail = autoReportEmail.trim(),
                autoReportFormat = autoReportFormat
            )
            repository.updateProfile(updated)
            _actionMessage.value = if (language == "am") "የንግድ ቅንብሮች ተቀምጠዋል! ✨" else "Settings saved successfully! ✨"
        }
    }

    fun setAutoReport(
        enabled: Boolean,
        frequency: String = businessProfile.value.autoReportFrequency,
        email: String = businessProfile.value.autoReportEmail,
        format: String = businessProfile.value.autoReportFormat
    ) {
        viewModelScope.launch {
            val updated = businessProfile.value.copy(
                autoReportEnabled = enabled,
                autoReportFrequency = frequency,
                autoReportEmail = email.trim(),
                autoReportFormat = format
            )
            repository.updateProfile(updated)
            val lang = updated.language
            val freqLabel = when (frequency.uppercase()) {
                "DAILY" -> if (lang == "am") "በየቀኑ" else "Daily"
                "WEEKLY" -> if (lang == "am") "በየሳምንቱ" else "Weekly"
                else -> if (lang == "am") "በየወሩ" else "Monthly"
            }
            _actionMessage.value = if (enabled) {
                if (lang == "am") "ራስ-ሰር ሪፖርት በርቷል! ⚡ ($freqLabel)" else "Auto-Report is now ON! ⚡ ($freqLabel)"
            } else {
                if (lang == "am") "ራስ-ሰር ሪፖርት ጠፍቷል (OFF)" else "Auto-Report turned OFF"
            }
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            val updated = businessProfile.value.copy(language = language)
            repository.updateProfile(updated)
            _actionMessage.value = if (language == "am") "ቋንቋ ወደ አማርኛ ተቀይሯል! 🇪🇹" else "Language changed to English! 🌐"
        }
    }

    private fun getStartOfDayMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getEndOfDayMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    private fun getStartOfWeekMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_YEAR, -6)
        return cal.timeInMillis
    }

    private fun getStartOfMonthMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}

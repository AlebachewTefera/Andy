package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.entity.Customer
import com.example.data.entity.Product
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = AppRepository(
            productDao = database.productDao(),
            saleDao = database.saleDao(),
            customerDao = database.customerDao(),
            expenseDao = database.expenseDao(),
            businessProfileDao = database.businessProfileDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TinyBiz", appName)
    }

    @Test
    fun `selling product automatically reduces remaining stock`() = runBlocking {
        val productId = repository.insertProduct(
            Product(name = "Strawberry Shortcake", price = 6.50, stockQuantity = 10, iconKey = "cupcake")
        )

        val beforeProduct = repository.getProductById(productId)
        assertEquals(10, beforeProduct?.stockQuantity)

        repository.recordSale(
            productId = productId,
            productName = "Strawberry Shortcake",
            price = 6.50,
            quantity = 3,
            customerId = null,
            customerName = "Guest",
            notes = "Test sale"
        )

        val afterProduct = repository.getProductById(productId)
        assertEquals(7, afterProduct?.stockQuantity)

        val sales = repository.allSales.first()
        assertEquals(1, sales.size)
        assertEquals(19.50, sales[0].totalAmount, 0.001)
    }

    @Test
    fun `profile language setting persists and supports Amharic`() = runBlocking {
        val initialProfile = com.example.data.entity.BusinessProfile(
            id = 1,
            businessName = "Addis Boutique",
            language = "am",
            currencySymbol = "Br"
        )
        repository.updateProfile(initialProfile)

        val retrieved = repository.businessProfile.first()
        assertEquals("Addis Boutique", retrieved?.businessName)
        assertEquals("am", retrieved?.language)
        assertEquals("Br", retrieved?.currencySymbol)
    }

    @Test
    fun `excel file and email body can be generated successfully`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val profile = com.example.data.entity.BusinessProfile(
            businessName = "Test Shop",
            currencySymbol = "$"
        )
        val report = com.example.ui.ReportSummary(
            period = com.example.ui.ReportPeriod.DAILY,
            totalSales = 150.0,
            totalExpenses = 45.0,
            totalProfit = 105.0,
            totalUnitsSold = 12,
            totalStockRemaining = 50,
            topSellingProducts = listOf("Cake" to 10),
            recentTransactions = emptyList(),
            recentExpenses = emptyList()
        )
        val strings = com.example.ui.util.getAppStrings("en")

        val excelFile = com.example.ui.util.ExcelExporter.createExcelFile(context, report, profile, strings)
        assertTrue(excelFile.exists())
        assertTrue(excelFile.length() > 0)
        assertTrue(excelFile.name.endsWith(".xlsx"))

        val emailBody = com.example.ui.util.ExcelExporter.buildEmailBody(report, profile, strings)
        assertTrue(emailBody.contains("Test Shop"))
        assertTrue(emailBody.contains("150.00"))
        assertTrue(emailBody.contains(".xlsx"))
    }

    @Test
    fun `auto report settings can be enabled and persist with custom frequency`() = runBlocking {
        val initialProfile = com.example.data.entity.BusinessProfile(
            id = 1,
            businessName = "Addis Boutique",
            autoReportEnabled = true,
            autoReportFrequency = "WEEKLY",
            autoReportEmail = "accountant@addis.com",
            autoReportFormat = "EXCEL"
        )
        repository.updateProfile(initialProfile)

        val retrieved = repository.businessProfile.first()
        assertEquals(true, retrieved?.autoReportEnabled)
        assertEquals("WEEKLY", retrieved?.autoReportFrequency)
        assertEquals("accountant@addis.com", retrieved?.autoReportEmail)
        assertEquals("EXCEL", retrieved?.autoReportFormat)
    }
}

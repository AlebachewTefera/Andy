package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

@Database(
    entities = [
        Product::class,
        Sale::class,
        Customer::class,
        Expense::class,
        BusinessProfile::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun customerDao(): CustomerDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun businessProfileDao(): BusinessProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE business_profile ADD COLUMN language TEXT NOT NULL DEFAULT 'en'")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE business_profile ADD COLUMN autoReportEnabled INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN autoReportFrequency TEXT NOT NULL DEFAULT 'DAILY'")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN autoReportEmail TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN autoReportFormat TEXT NOT NULL DEFAULT 'EXCEL'")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tinybiz_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

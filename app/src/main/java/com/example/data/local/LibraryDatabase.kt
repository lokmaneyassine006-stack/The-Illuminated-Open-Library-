package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Book
import com.example.data.model.PaymentSettings
import com.example.data.model.PurchaseOrder
import com.example.data.model.Review

@Database(
    entities = [Book::class, PaymentSettings::class, PurchaseOrder::class, Review::class],
    version = 2,
    exportSchema = false
)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun paymentSettingsDao(): PaymentSettingsDao
    abstract fun purchaseOrderDao(): PurchaseOrderDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: LibraryDatabase? = null

        fun getInstance(context: Context): LibraryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LibraryDatabase::class.java,
                    "illuminated_library_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

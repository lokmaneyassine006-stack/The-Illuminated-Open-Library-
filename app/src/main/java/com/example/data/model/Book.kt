package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String = "المؤلف",
    val category: String = "عام",
    val description: String = "",
    val sampleContent: String = "",
    val fullContent: String = "",
    val pageCount: Int = 100,
    val isFree: Boolean = false,
    val priceDzd: Double = 1000.0,      // السعر بالدينار الجزائري لبريدي موب
    val priceUsdt: Double = 5.0,        // السعر بالدولار / USDT لبينانس
    val publishedPeriod: String = "2026", // فترة النشر
    val coverStyleIndex: Int = 0,       // تدرج لوني فني لغلاف الكتاب
    val coverImageUrl: String = "",     // رابط أو مسار صورة الغلاف
    val isUnlocked: Boolean = false,    // هل تم الشراء أو الفتح
    val createdAt: Long = System.currentTimeMillis()
)

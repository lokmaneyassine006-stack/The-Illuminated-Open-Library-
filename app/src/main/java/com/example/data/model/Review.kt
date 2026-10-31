package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing reader feedback, ratings, and testimonials.
 */
@Entity(tableName = "reader_reviews")
data class Review(
    @PrimaryKey(autoGenerate = true)
    val reviewId: Long = 0,
    val bookId: Long,
    val bookTitle: String,
    val readerName: String,
    val rating: Int = 5,                  // 1 to 5 stars
    val comment: String,
    val paymentMethod: String = "",       // "BARIDIMOB" or "BINANCE" or "DIRECT"
    val timestamp: Long = System.currentTimeMillis(),
    val isVerifiedPurchase: Boolean = true
)

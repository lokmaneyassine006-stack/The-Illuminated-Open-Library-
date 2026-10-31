package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchase_orders")
data class PurchaseOrder(
    @PrimaryKey(autoGenerate = true)
    val orderId: Long = 0,
    val bookId: Long,
    val bookTitle: String,
    val paymentMethod: String,          // "BARIDIMOB" or "BINANCE"
    val amountFormatted: String,        // e.g. "1500 DZD" or "7.50 USDT"
    val transactionReference: String,   // رقم المعاملة أو وصل التحويل
    val customerContact: String = "",   // هاتف أو بريد المشتري
    val customerNote: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "CONFIRMED"    // "PENDING", "CONFIRMED"
)

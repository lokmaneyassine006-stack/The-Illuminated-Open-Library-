package com.example

import com.example.data.model.Book
import com.example.data.model.PurchaseOrder
import com.example.data.model.Review
import com.example.ui.viewmodel.ReviewsSummary
import com.example.ui.viewmodel.SalesSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CreatorDashboardTest {

    @Test
    fun `test Review model properties and verification`() {
        val review = Review(
            reviewId = 1L,
            bookId = 10L,
            bookTitle = "أنوار الحكمة والمعرفة الرقمية",
            readerName = "ياسين بن عيسى",
            rating = 5,
            comment = "كتاب استثنائي ومحتوى فكري رصين. الدفع عبر بريدي موب كان سريعاً جداً.",
            paymentMethod = "BARIDIMOB",
            isVerifiedPurchase = true
        )

        assertEquals(1L, review.reviewId)
        assertEquals(10L, review.bookId)
        assertEquals("أنوار الحكمة والمعرفة الرقمية", review.bookTitle)
        assertEquals("ياسين بن عيسى", review.readerName)
        assertEquals(5, review.rating)
        assertTrue(review.comment.contains("بريدي موب"))
        assertEquals("BARIDIMOB", review.paymentMethod)
        assertTrue(review.isVerifiedPurchase)
    }

    @Test
    fun `test SalesSummary calculations for BaridiMob and Binance`() {
        val orders = listOf(
            PurchaseOrder(
                bookId = 1L,
                bookTitle = "كتاب 1",
                paymentMethod = "BARIDIMOB",
                amountFormatted = "1,500 د.ج",
                transactionReference = "BM-001",
                status = "CONFIRMED"
            ),
            PurchaseOrder(
                bookId = 2L,
                bookTitle = "كتاب 2",
                paymentMethod = "BARIDIMOB",
                amountFormatted = "2,000 د.ج",
                transactionReference = "BM-002",
                status = "CONFIRMED"
            ),
            PurchaseOrder(
                bookId = 1L,
                bookTitle = "كتاب 1",
                paymentMethod = "BINANCE",
                amountFormatted = "15.00 USDT",
                transactionReference = "TX-001",
                status = "CONFIRMED"
            ),
            PurchaseOrder(
                bookId = 3L,
                bookTitle = "كتاب 3",
                paymentMethod = "BINANCE",
                amountFormatted = "10.00 USDT",
                transactionReference = "TX-002",
                status = "PENDING"
            )
        )

        val totalDzd = orders.filter { it.paymentMethod == "BARIDIMOB" }
            .sumOf { it.amountFormatted.replace(",", "").replace("د.ج", "").trim().toDouble() }
        val totalUsdt = orders.filter { it.paymentMethod == "BINANCE" }
            .sumOf { it.amountFormatted.replace("USDT", "").trim().toDouble() }

        val summary = SalesSummary(
            totalDzd = totalDzd,
            totalUsdt = totalUsdt,
            totalOrders = orders.size,
            confirmedOrders = orders.count { it.status == "CONFIRMED" },
            pendingOrders = orders.count { it.status == "PENDING" },
            baridiMobOrdersCount = orders.count { it.paymentMethod == "BARIDIMOB" },
            binanceOrdersCount = orders.count { it.paymentMethod == "BINANCE" }
        )

        assertEquals(3500.0, summary.totalDzd, 0.01)
        assertEquals(25.0, summary.totalUsdt, 0.01)
        assertEquals(4, summary.totalOrders)
        assertEquals(3, summary.confirmedOrders)
        assertEquals(1, summary.pendingOrders)
        assertEquals(2, summary.baridiMobOrdersCount)
        assertEquals(2, summary.binanceOrdersCount)
    }

    @Test
    fun `test ReviewsSummary average rating and star counts`() {
        val reviews = listOf(
            Review(bookId = 1L, bookTitle = "كتاب 1", readerName = "أحمد", rating = 5, comment = "ممتاز"),
            Review(bookId = 1L, bookTitle = "كتاب 1", readerName = "سليم", rating = 5, comment = "رائع جداً"),
            Review(bookId = 2L, bookTitle = "كتاب 2", readerName = "كريم", rating = 4, comment = "جيد جداً"),
            Review(bookId = 2L, bookTitle = "كتاب 2", readerName = "فاطمة", rating = 5, comment = "أنصح به")
        )

        val avg = reviews.map { it.rating }.average()
        val counts = mutableMapOf(5 to 0, 4 to 0, 3 to 0, 2 to 0, 1 to 0)
        reviews.forEach { counts[it.rating] = (counts[it.rating] ?: 0) + 1 }

        val summary = ReviewsSummary(
            totalReviews = reviews.size,
            averageRating = avg,
            starCounts = counts
        )

        assertEquals(4, summary.totalReviews)
        assertEquals(4.75, summary.averageRating, 0.01)
        assertEquals(3, summary.starCounts[5])
        assertEquals(1, summary.starCounts[4])
        assertEquals(0, summary.starCounts[3])
    }
}

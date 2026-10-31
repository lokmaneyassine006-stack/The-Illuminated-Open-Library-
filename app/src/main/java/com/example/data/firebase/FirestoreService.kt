package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.Book
import com.example.data.model.PaymentSettings
import com.example.data.model.PurchaseOrder
import com.example.data.model.Review
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreService"

/**
 * Service providing cloud data synchronization with Firebase Firestore Enterprise.
 * Strictly uses the configured custom database ID from `firebase_applet_config.xml`.
 */
class FirestoreService(private val context: Context) {

    val firestore: FirebaseFirestore by lazy {
        val databaseId = context.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(FirebaseApp.getInstance(), databaseId)
    }

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    val currentUserId: String? get() = auth.currentUser?.uid

    private fun requireUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("يجب تسجيل الدخول بحساب Google أولاً للمزامنة السحابية.")
    }

    /**
     * Synchronizes a book to the cloud Firestore `/books/{bookId}` collection.
     */
    suspend fun syncBookToCloud(book: Book): Boolean {
        val uid = auth.currentUser?.uid ?: run {
            Log.w(TAG, "Cannot sync book to Firestore: user not authenticated")
            return false
        }
        return try {
            val bookDocId = if (book.id > 0) book.id.toString() else firestore.collection("books").document().id
            val bookData = hashMapOf(
                "title" to book.title,
                "author" to book.author,
                "authorId" to uid,
                "description" to book.description,
                "coverImageUrl" to book.coverImageUrl,
                "priceDzd" to book.priceDzd,
                "priceUsdt" to book.priceUsdt,
                "isFree" to book.isFree,
                "category" to book.category,
                "pageCount" to book.pageCount,
                "publishedPeriod" to book.publishedPeriod,
                "coverStyleIndex" to book.coverStyleIndex,
                "sampleContent" to book.sampleContent,
                "fullContent" to book.fullContent,
                "createdAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("books").document(bookDocId).set(bookData, SetOptions.merge()).await()
            Log.d(TAG, "Book synced successfully to Firestore: $bookDocId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync book to Firestore", e)
            false
        }
    }

    /**
     * Listens for realtime updates to published books.
     */
    fun observeCloudBooks(): Flow<List<Book>> = callbackFlow {
        if (auth.currentUser == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val registration = firestore.collection("books")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for books", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val books = snapshot.documents.mapNotNull { doc ->
                        try {
                            Book(
                                id = doc.id.toLongOrNull() ?: doc.id.hashCode().toLong().coerceAtLeast(1L),
                                title = doc.getString("title") ?: "",
                                author = doc.getString("author") ?: "",
                                description = doc.getString("description") ?: "",
                                priceDzd = doc.getDouble("priceDzd") ?: 0.0,
                                priceUsdt = doc.getDouble("priceUsdt") ?: 0.0,
                                isFree = doc.getBoolean("isFree") ?: false,
                                category = doc.getString("category") ?: "عام",
                                pageCount = (doc.getLong("pageCount") ?: 100L).toInt(),
                                publishedPeriod = doc.getString("publishedPeriod") ?: "2026",
                                coverStyleIndex = (doc.getLong("coverStyleIndex") ?: 0L).toInt(),
                                coverImageUrl = doc.getString("coverImageUrl") ?: "",
                                sampleContent = doc.getString("sampleContent") ?: "",
                                fullContent = doc.getString("fullContent") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(books)
                }
            }
        awaitClose { registration.remove() }
    }

    /**
     * Stores author payment settings (BaridiMob & Binance).
     */
    suspend fun syncPaymentSettings(settings: PaymentSettings): Boolean {
        val uid = requireUserId()
        return try {
            val data = hashMapOf(
                "authorId" to uid,
                "baridiMobRip" to settings.baridiMobRip,
                "baridiMobAccountHolder" to settings.baridiMobAccountHolder,
                "baridiMobPhone" to settings.baridiMobPhone,
                "baridiMobCcpNumber" to settings.baridiMobCcpNumber,
                "binancePayId" to settings.binancePayId,
                "binanceUsdtAddress" to settings.binanceUsdtAddress,
                "binanceNetwork" to settings.binanceNetwork,
                "binanceEmail" to settings.binanceEmail,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("payment_settings").document(uid).set(data, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync payment settings", e)
            false
        }
    }

    /**
     * Submits purchase transaction to Firestore.
     */
    suspend fun submitPurchaseOrder(order: PurchaseOrder): Boolean {
        val uid = requireUserId()
        return try {
            val orderData = hashMapOf(
                "bookId" to order.bookId.toString(),
                "bookTitle" to order.bookTitle,
                "buyerId" to uid,
                "paymentMethod" to order.paymentMethod,
                "amountFormatted" to order.amountFormatted,
                "transactionReference" to order.transactionReference,
                "customerContact" to order.customerContact,
                "customerNote" to order.customerNote,
                "status" to order.status,
                "createdAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("orders").document().set(orderData).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to submit purchase order to Firestore", e)
            false
        }
    }

    /**
     * Submits reader review to Firestore.
     */
    suspend fun submitReviewToCloud(review: Review): Boolean {
        val uid = requireUserId()
        return try {
            val reviewData = hashMapOf(
                "bookId" to review.bookId.toString(),
                "bookTitle" to review.bookTitle,
                "authorOrReaderId" to uid,
                "readerName" to review.readerName,
                "rating" to review.rating,
                "comment" to review.comment,
                "paymentMethod" to review.paymentMethod,
                "isVerifiedPurchase" to review.isVerifiedPurchase,
                "createdAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("reviews").document().set(reviewData).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to submit review to Firestore", e)
            false
        }
    }
}

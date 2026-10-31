package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAiService
import com.example.data.firebase.FirestoreService
import com.example.data.local.LibraryDatabase
import com.example.data.model.Book
import com.example.data.model.PaymentSettings
import com.example.data.model.PurchaseOrder
import com.example.data.model.Review
import com.example.data.repository.LibraryRepository
import com.example.data.auth.AuthManager
import com.example.data.auth.AuthUser
import com.example.data.auth.toAuthUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "LibraryViewModel"

enum class AppScreen {
    CATALOG,
    CREATOR_DASHBOARD
}

enum class CreatorTab {
    SALES,
    REVIEWS
}

data class SalesSummary(
    val totalDzd: Double = 0.0,
    val totalUsdt: Double = 0.0,
    val totalOrders: Int = 0,
    val confirmedOrders: Int = 0,
    val pendingOrders: Int = 0,
    val baridiMobOrdersCount: Int = 0,
    val binanceOrdersCount: Int = 0
)

data class ReviewsSummary(
    val totalReviews: Int = 0,
    val averageRating: Double = 5.0,
    val starCounts: Map<Int, Int> = mapOf(5 to 0, 4 to 0, 3 to 0, 2 to 0, 1 to 0)
)

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LibraryRepository
    private val aiService = GeminiAiService()
    private val firestoreService = FirestoreService(application)

    val allBooks: StateFlow<List<Book>>
    val paymentSettings: StateFlow<PaymentSettings>
    val orders: StateFlow<List<PurchaseOrder>>
    val allReviews: StateFlow<List<Review>>

    // Navigation & Tabs
    val currentScreen = MutableStateFlow(AppScreen.CATALOG)
    val creatorSelectedTab = MutableStateFlow(CreatorTab.SALES)
    val isAddReviewDialogOpen = MutableStateFlow(false)
    val isRecordSaleDialogOpen = MutableStateFlow(false)

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("الكل")
    val selectedPriceFilter = MutableStateFlow("الكل") // "الكل", "مجاني", "مدفوع"

    // Dialog & Navigation states
    val isAddBookDialogOpen = MutableStateFlow(false)
    val isSavingBook = MutableStateFlow(false)
    val isPaymentSettingsDialogOpen = MutableStateFlow(false)
    val bookToEditPrice = MutableStateFlow<Book?>(null)
    val bookToEdit = MutableStateFlow<Book?>(null)
    val bookForCheckout = MutableStateFlow<Book?>(null)
    val bookForReading = MutableStateFlow<Book?>(null)
    val isOrdersDialogOpen = MutableStateFlow(false)

    // Gemini AI States
    val bookForAiAssistant = MutableStateFlow<Book?>(null)
    private val _aiDraftState = MutableStateFlow<AiDraftState>(AiDraftState.Idle)
    val aiDraftState: StateFlow<AiDraftState> = _aiDraftState.asStateFlow()

    private val _aiReaderState = MutableStateFlow<AiReaderState>(AiReaderState.Idle)
    val aiReaderState: StateFlow<AiReaderState> = _aiReaderState.asStateFlow()

    private var activeAiJob: Job? = null

    // Auth States
    val authUser: StateFlow<AuthUser?> = AuthManager.userFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthManager.currentUser?.toAuthUser()
    )
    val isAuthDialogOpen = MutableStateFlow(false)
    val isAuthLoading = MutableStateFlow(false)
    val authErrorMessage = MutableStateFlow<String?>(null)

    // User feedback message
    val userSnackbarMessage = MutableStateFlow<String?>(null)

    init {
        val db = LibraryDatabase.getInstance(application)
        repository = LibraryRepository(
            bookDao = db.bookDao(),
            paymentSettingsDao = db.paymentSettingsDao(),
            purchaseOrderDao = db.purchaseOrderDao(),
            reviewDao = db.reviewDao()
        )

        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }

        allBooks = repository.allBooks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        paymentSettings = repository.paymentSettings.combine(
            MutableStateFlow(PaymentSettings())
        ) { dbSettings, defaultSettings ->
            dbSettings ?: defaultSettings
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PaymentSettings()
        )

        orders = repository.allOrders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allReviews = repository.allReviews.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    val salesSummary: StateFlow<SalesSummary> = orders.map { orderList ->
        var totalDzd = 0.0
        var totalUsdt = 0.0
        var confirmed = 0
        var pending = 0
        var baridiMobCount = 0
        var binanceCount = 0

        for (order in orderList) {
            val amountNum = parseAmount(order.amountFormatted)
            if (order.paymentMethod.equals("BARIDIMOB", ignoreCase = true)) {
                baridiMobCount++
                totalDzd += amountNum
            } else if (order.paymentMethod.equals("BINANCE", ignoreCase = true)) {
                binanceCount++
                totalUsdt += amountNum
            }

            if (order.status.equals("CONFIRMED", ignoreCase = true)) {
                confirmed++
            } else {
                pending++
            }
        }

        SalesSummary(
            totalDzd = totalDzd,
            totalUsdt = totalUsdt,
            totalOrders = orderList.size,
            confirmedOrders = confirmed,
            pendingOrders = pending,
            baridiMobOrdersCount = baridiMobCount,
            binanceOrdersCount = binanceCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SalesSummary()
    )

    val reviewsSummary: StateFlow<ReviewsSummary> = allReviews.map { reviewList ->
        if (reviewList.isEmpty()) {
            ReviewsSummary()
        } else {
            val avg = reviewList.map { it.rating }.average()
            val counts = mutableMapOf(5 to 0, 4 to 0, 3 to 0, 2 to 0, 1 to 0)
            for (r in reviewList) {
                counts[r.rating] = (counts[r.rating] ?: 0) + 1
            }
            ReviewsSummary(
                totalReviews = reviewList.size,
                averageRating = if (avg.isNaN()) 5.0 else avg,
                starCounts = counts
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReviewsSummary()
    )

    val filteredBooks: StateFlow<List<Book>> = combine(
        allBooks,
        searchQuery,
        selectedCategory,
        selectedPriceFilter
    ) { books, query, category, priceFilter ->
        books.filter { book ->
            val matchesQuery = query.isBlank() ||
                    book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true) ||
                    book.description.contains(query, ignoreCase = true) ||
                    book.category.contains(query, ignoreCase = true)

            val matchesCategory = category == "الكل" || book.category == category

            val matchesPrice = when (priceFilter) {
                "مجاني" -> book.isFree || book.priceDzd <= 0.0
                "مدفوع" -> !book.isFree && book.priceDzd > 0.0
                else -> true
            }

            matchesQuery && matchesCategory && matchesPrice
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveBook(book: Book) {
        viewModelScope.launch {
            isSavingBook.value = true
            try {
                var cloudSynced = false
                if (book.id == 0L) {
                    val newId = repository.insertBook(book)
                    val bookWithId = book.copy(id = newId)
                    if (authUser.value != null) {
                        cloudSynced = firestoreService.syncBookToCloud(bookWithId)
                    }
                    userSnackbarMessage.value = if (cloudSynced) {
                        "تم حفظ كتاب \"${book.title}\" ومزامنته مع Firestore السحابية بنجاح ☁️"
                    } else {
                        "تمت إضافة كتاب \"${book.title}\" بنجاح للمكتبة!"
                    }
                } else {
                    repository.updateBook(book)
                    if (authUser.value != null) {
                        cloudSynced = firestoreService.syncBookToCloud(book)
                    }
                    userSnackbarMessage.value = if (cloudSynced) {
                        "تم تحديث كتاب \"${book.title}\" في Firestore السحابية بنجاح ☁️"
                    } else {
                        "تم تعديل كتاب \"${book.title}\" بنجاح!"
                    }
                }
                isAddBookDialogOpen.value = false
                bookToEdit.value = null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save book", e)
                userSnackbarMessage.value = "حدث خطأ أثناء حفظ الكتاب: ${e.localizedMessage}"
            } finally {
                isSavingBook.value = false
            }
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
            userSnackbarMessage.value = "تم حذف كتاب \"${book.title}\" من المكتبة."
        }
    }

    fun updateBookPricing(bookId: Long, priceDzd: Double, priceUsdt: Double, isFree: Boolean) {
        viewModelScope.launch {
            repository.updateBookPrice(bookId, priceDzd, priceUsdt, isFree)
            bookToEditPrice.value = null
            userSnackbarMessage.value = if (isFree) "تم جعل الكتاب مجانياً!" else "تم تحديث سعر الكتاب بنجاح ($priceDzd د.ج / $priceUsdt USDT)"
        }
    }

    fun savePaymentSettings(settings: PaymentSettings) {
        viewModelScope.launch {
            repository.savePaymentSettings(settings)
            if (authUser.value != null) {
                firestoreService.syncPaymentSettings(settings)
            }
            isPaymentSettingsDialogOpen.value = false
            userSnackbarMessage.value = "تم حفظ بيانات الدفع (بريدي موب وبينانس) ومزامنتها بنجاح!"
        }
    }

    fun submitPurchaseOrder(
        book: Book,
        paymentMethod: String,
        amountFormatted: String,
        referenceNumber: String,
        customerContact: String,
        buyerNote: String
    ) {
        viewModelScope.launch {
            val order = PurchaseOrder(
                bookId = book.id,
                bookTitle = book.title,
                paymentMethod = paymentMethod,
                amountFormatted = amountFormatted,
                transactionReference = referenceNumber,
                customerContact = customerContact,
                customerNote = buyerNote,
                status = "CONFIRMED"
            )
            repository.insertOrder(order)
            if (authUser.value != null) {
                firestoreService.submitPurchaseOrder(order)
            }
            repository.unlockBook(book.id)
            bookForCheckout.value = null
            userSnackbarMessage.value = "تم تأكيد طلبك بنجاح وفتح كتاب \"${book.title}\" للقراءة!"
        }
    }

    fun deleteOrder(orderId: Long) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
            userSnackbarMessage.value = "تم حذف الطلب من السجل."
        }
    }

    // --- Gemini AI Actions ---

    /**
     * Use Gemini to generate author book draft (description, sample, category, suggested prices).
     */
    fun generateBookDraftWithAi(titleOrTopic: String) {
        if (titleOrTopic.isBlank()) {
            _aiDraftState.value = AiDraftState.Error("يرجى إدخال عنوان أو فكرة الكتاب أولاً.")
            return
        }

        viewModelScope.launch {
            _aiDraftState.value = AiDraftState.Loading
            try {
                val draft = aiService.generateBookDraft(titleOrTopic)
                _aiDraftState.value = AiDraftState.Success(draft)
                userSnackbarMessage.value = "تم إنشاء مسودة الكتاب وتسعيره المقترح بنجاح عبر Gemini ✨"
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Gemini draft timed out", e)
                _aiDraftState.value = AiDraftState.Error("استغرقت العملية أكثر من 60 ثانية. يرجى إعادة المحاولة.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Gemini draft generation failed", e)
                _aiDraftState.value = AiDraftState.Error(
                    e.localizedMessage ?: "فشل توليد محتوى الكتاب بالذكاء الاصطناعي."
                )
            }
        }
    }

    fun resetAiDraftState() {
        _aiDraftState.value = AiDraftState.Idle
    }

    /**
     * Ask Gemini questions or summarize a book with streaming text.
     */
    fun askGeminiAboutBook(book: Book, question: String) {
        activeAiJob?.cancel()
        activeAiJob = viewModelScope.launch {
            _aiReaderState.value = AiReaderState.Loading
            val accumulated = java.lang.StringBuilder()
            try {
                val contentForAi = if (book.fullContent.isNotBlank()) {
                    book.fullContent.take(3000)
                } else {
                    "${book.description}\n\n${book.sampleContent}".take(3000)
                }

                aiService.askBookAssistantStream(
                    bookTitle = book.title,
                    author = book.author,
                    content = contentForAi,
                    userQuestion = question
                ).collect { chunk ->
                    accumulated.append(chunk)
                    _aiReaderState.value = AiReaderState.Streaming(accumulated.toString())
                }

                if (accumulated.isNotBlank()) {
                    _aiReaderState.value = AiReaderState.Success(accumulated.toString())
                } else {
                    _aiReaderState.value = AiReaderState.Error("لم يُرجع الذكاء الاصطناعي أي رد.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Gemini ask timed out", e)
                _aiReaderState.value = AiReaderState.Error("انتهت مهلة استجابة Gemini.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Gemini streaming failed", e)
                _aiReaderState.value = AiReaderState.Error(
                    e.localizedMessage ?: "حدث خطأ أثناء التواصل مع Gemini."
                )
            }
        }
    }

    fun resetAiReaderState() {
        activeAiJob?.cancel()
        _aiReaderState.value = AiReaderState.Idle
    }

    fun openAuthDialog() {
        authErrorMessage.value = null
        isAuthDialogOpen.value = true
    }

    fun closeAuthDialog() {
        isAuthDialogOpen.value = false
        authErrorMessage.value = null
        isAuthLoading.value = false
    }

    fun signInWithGoogle(activity: android.app.Activity) {
        val credentialManager = androidx.credentials.CredentialManager.create(activity)
        isAuthLoading.value = true
        authErrorMessage.value = null

        com.example.data.auth.AuthManager.signInWithGoogle(
            activity = activity,
            credentialManager = credentialManager,
            scope = viewModelScope,
            onAuthSuccess = { user ->
                isAuthLoading.value = false
                userSnackbarMessage.value = "مرحباً بك ${user.displayName ?: "يا قارئنا العزيز"}! تم تسجيل الدخول بنجاح."
                closeAuthDialog()
            },
            onAuthError = { err ->
                isAuthLoading.value = false
                authErrorMessage.value = err
            },
            onAuthCancelled = {
                isAuthLoading.value = false
            }
        )
    }

    fun signOut(context: android.content.Context) {
        val credentialManager = androidx.credentials.CredentialManager.create(context)
        isAuthLoading.value = true
        com.example.data.auth.AuthManager.signOut(
            credentialManager = credentialManager,
            scope = viewModelScope,
            onSignOutComplete = {
                isAuthLoading.value = false
                userSnackbarMessage.value = "تم تسجيل الخروج بنجاح."
                closeAuthDialog()
            }
        )
    }

    // --- Creator Studio Actions ---

    fun selectScreen(screen: AppScreen) {
        currentScreen.value = screen
    }

    fun selectCreatorTab(tab: CreatorTab) {
        creatorSelectedTab.value = tab
    }

    fun toggleOrderStatus(order: PurchaseOrder) {
        viewModelScope.launch {
            val newStatus = if (order.status.equals("CONFIRMED", ignoreCase = true)) "PENDING" else "CONFIRMED"
            repository.updateOrderStatus(order.orderId, newStatus)
            userSnackbarMessage.value = if (newStatus == "CONFIRMED") "تم تأكيد استلام الدفعة بنجاح!" else "تم تحويل الطلب إلى قيد المراجعة."
        }
    }

    fun recordManualSale(
        book: Book,
        paymentMethod: String,
        amountFormatted: String,
        transactionRef: String,
        contact: String,
        note: String
    ) {
        viewModelScope.launch {
            val ref = if (transactionRef.isNotBlank()) transactionRef else "MANUAL-${System.currentTimeMillis() % 100000}"
            val order = PurchaseOrder(
                bookId = book.id,
                bookTitle = book.title,
                paymentMethod = paymentMethod,
                amountFormatted = amountFormatted,
                transactionReference = ref,
                customerContact = contact,
                customerNote = note,
                status = "CONFIRMED"
            )
            repository.insertOrder(order)
            if (authUser.value != null) {
                firestoreService.submitPurchaseOrder(order)
            }
            isRecordSaleDialogOpen.value = false
            userSnackbarMessage.value = "تم تسجيل المبيعة بنجاح في سجل المبيعات 📈"
        }
    }

    fun saveReview(
        book: Book,
        readerName: String,
        rating: Int,
        comment: String,
        paymentMethod: String,
        isVerifiedPurchase: Boolean
    ) {
        viewModelScope.launch {
            val review = Review(
                bookId = book.id,
                bookTitle = book.title,
                readerName = if (readerName.isNotBlank()) readerName else "قارئ كريم",
                rating = rating.coerceIn(1, 5),
                comment = comment.trim(),
                paymentMethod = paymentMethod,
                isVerifiedPurchase = isVerifiedPurchase
            )
            repository.insertReview(review)
            if (authUser.value != null) {
                firestoreService.submitReviewToCloud(review)
            }
            isAddReviewDialogOpen.value = false
            userSnackbarMessage.value = "شكراً لك! تم إضافة رأي القارئ بنجاح ⭐"
        }
    }

    fun deleteReview(review: Review) {
        viewModelScope.launch {
            repository.deleteReview(review.reviewId)
            userSnackbarMessage.value = "تم حذف الرأي."
        }
    }

    private fun parseAmount(amountStr: String): Double {
        return try {
            val clean = amountStr
                .replace(",", "")
                .replace("د.ج", "")
                .replace("DZD", "")
                .replace("USDT", "")
                .replace("$", "")
                .trim()
            clean.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    fun clearSnackbar() {
        userSnackbarMessage.value = null
    }
}

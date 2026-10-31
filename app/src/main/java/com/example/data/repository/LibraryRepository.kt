package com.example.data.repository

import com.example.data.local.BookDao
import com.example.data.local.PaymentSettingsDao
import com.example.data.local.PurchaseOrderDao
import com.example.data.local.ReviewDao
import com.example.data.model.Book
import com.example.data.model.PaymentSettings
import com.example.data.model.PurchaseOrder
import com.example.data.model.Review
import kotlinx.coroutines.flow.Flow

class LibraryRepository(
    private val bookDao: BookDao,
    private val paymentSettingsDao: PaymentSettingsDao,
    private val purchaseOrderDao: PurchaseOrderDao,
    private val reviewDao: ReviewDao
) {
    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()
    val paymentSettings: Flow<PaymentSettings?> = paymentSettingsDao.getSettingsFlow()
    val allOrders: Flow<List<PurchaseOrder>> = purchaseOrderDao.getAllOrders()
    val allReviews: Flow<List<Review>> = reviewDao.getAllReviews()

    suspend fun initializeDefaultDataIfEmpty() {
        if (bookDao.getBookCount() == 0) {
            val starterBooks = listOf(
                Book(
                    title = "أنوار الحكمة والمعرفة الرقمية",
                    author = "الكاتب المؤسس",
                    category = "فكر وفلسفة",
                    description = "دراسة معمقة في تقاطع الفكر الإنساني مع عصر المعلومات، واستشراف آفاق الإدراك والمعرفة في القرن الحادي والعشرين.",
                    sampleContent = """
                        الفصل الأول: فجر البصيرة
                        
                        في هذا العصر المتسارع، تتلاقى ومضات الفكر القديم مع أنوار التقنية الحديثة لتشكل نسقاً معرفياً فريداً. لم يعد الكتاب مجرد صفحات مرقومة، بل نافذة تنفتح على عوالم شاسعة من الاستبصار والابتكار.
                        
                        إن المعرفة الحقيقية هي التي تضيء دروب الإنسان، وتمنحه القدرة على التمييز بين الغث والسمين في بحر المعلومات الهادر.
                    """.trimIndent(),
                    fullContent = """
                        الفصل الأول: فجر البصيرة
                        في هذا العصر المتسارع، تتلاقى ومضات الفكر القديم مع أنوار التقنية الحديثة لتشكل نسقاً معرفياً فريداً.
                        
                        الفصل الثاني: البناء المعرفي المستدام
                        كيف نبني عقلاً قادراً على النقد والتحليل؟ البداية تكمن في اختيار المصادر الموثوقة والتأمل اليومي في المفاهيم الجوهرية.
                        
                        الفصل الثالث: تجليات الإبداع في العصر الرقمي
                        الذكاء والابتكار ليسا وليدي الصدفة، بل نتاج تلاقح الأفكار والعمل المستمر على صقل المهارات والاطلاع على أحدث ما توصل إليه الفكر العالمي.
                        
                        خاتمة: نحو مكتبة منيرة مفتوحة للجميع
                        إن مشاركة العلم هي الرسالة الأسمى التي تسمو بالإنسانية نحو مدارج الرقي والسلام.
                    """.trimIndent(),
                    pageCount = 180,
                    isFree = false,
                    priceDzd = 1200.0,
                    priceUsdt = 6.0,
                    publishedPeriod = "ربيع 2026",
                    coverStyleIndex = 0,
                    isUnlocked = false
                ),
                Book(
                    title = "الدليل الشامل للعملات الرقمية والبلوكتشين",
                    author = "خبير الاقتصاد الرقمي",
                    category = "تقنية ومال",
                    description = "دليل عملي وتطبيقي لفهم شبكات البلوكتشين، منصة Binance، والمدفوعات اللامركزية الحديثة.",
                    sampleContent = """
                        مقدمة الدليل:
                        
                        أحدثت تقنية البلوكتشين ثورة لا تقل أهمية عن ظهور شبكة الإنترنت. من خلال العقود الذكية والعملات الرقمية، أصبح الانتقال المالي عالمياً ولحظياً دون قيود جغرافية.
                    """.trimIndent(),
                    fullContent = """
                        مقدمة الدليل:
                        أحدثت تقنية البلوكتشين ثورة لا تقل أهمية عن ظهور شبكة الإنترنت.
                        
                        الباب الأول: أساسيات التداول والمحافظ الآمنة
                        كيف تحافظ على أمان أصولك الرقمية وتستخدم شبكات مثل TRC20 و BEP20 بكفاءة وأقل رسوم ممكنة.
                        
                        الباب الثاني: دمج المدفوعات الرقمية في المنصات
                        استخدام Binance Pay وطرق استلام الدفعات الفورية العابرة للحدود.
                    """.trimIndent(),
                    pageCount = 220,
                    isFree = false,
                    priceDzd = 2000.0,
                    priceUsdt = 10.0,
                    publishedPeriod = "شتاء 2026",
                    coverStyleIndex = 1,
                    isUnlocked = false
                ),
                Book(
                    title = "قبسات من التراث الجزائري والأندلسي",
                    author = "باحث في التراث",
                    category = "تاريخ وتراث",
                    description = "رحلة أدبية وتاريخية تسبر أغوار المخطوطات والآثار الفكرية المشتركة في بلاد المغرب والأندلس.",
                    sampleContent = """
                        المقدمة:
                        
                        تاريخ حافل بالضياء والأصالة، يروي حكاية المكتبات العريقة في تلمسان وقسنطينة وبجاية وغرناطة وكيف أنارت قناديل العلم ظلمات القرون الوسطى.
                    """.trimIndent(),
                    fullContent = """
                        المقدمة:
                        تاريخ حافل بالضياء والأصالة، يروي حكاية المكتبات العريقة.
                        
                        الفصل الأول: حواضر العلم في الجزائر عبر العصور.
                        الفصل الثاني: مكتبات المخطوطات النادرة وأسرار حفظها.
                        الفصل الثالث: الجسور الثقافية الخالدة.
                    """.trimIndent(),
                    pageCount = 150,
                    isFree = true,
                    priceDzd = 0.0,
                    priceUsdt = 0.0,
                    publishedPeriod = "2026",
                    coverStyleIndex = 2,
                    isUnlocked = true
                )
            )
            bookDao.insertAll(starterBooks)
        }

        if (paymentSettingsDao.getSettings() == null) {
            paymentSettingsDao.saveSettings(PaymentSettings())
        }

        // Initialize starter sales orders if empty for dashboard visualization
        if (purchaseOrderDao.getOrderCount() == 0) {
            val starterOrders = listOf(
                PurchaseOrder(
                    bookId = 1L,
                    bookTitle = "أنوار الحكمة والمعرفة الرقمية",
                    paymentMethod = "BARIDIMOB",
                    amountFormatted = "1,200 د.ج",
                    transactionReference = "BM-2026-984210",
                    customerContact = "0550 12 34 56",
                    customerNote = "تحويل بريدي موب من حساب الجلفة. بانتظار تفعيل النسخة.",
                    timestamp = System.currentTimeMillis() - 86400000L * 2,
                    status = "CONFIRMED"
                ),
                PurchaseOrder(
                    bookId = 2L,
                    bookTitle = "الدليل الشامل للعملات الرقمية والبلوكتشين",
                    paymentMethod = "BINANCE",
                    amountFormatted = "10.00 USDT",
                    transactionReference = "TX-BINANCE-882194301",
                    customerContact = "crypto.trader@gmail.com",
                    customerNote = "تم الدفع عبر Binance Pay (PayID: 29841029).",
                    timestamp = System.currentTimeMillis() - 86400000L * 1,
                    status = "CONFIRMED"
                ),
                PurchaseOrder(
                    bookId = 1L,
                    bookTitle = "أنوار الحكمة والمعرفة الرقمية",
                    paymentMethod = "BARIDIMOB",
                    amountFormatted = "1,200 د.ج",
                    transactionReference = "BM-2026-773125",
                    customerContact = "0661 98 76 54",
                    customerNote = "دفع بريدي موب من وهران. شكراً للمؤلف.",
                    timestamp = System.currentTimeMillis() - 3600000L * 5,
                    status = "CONFIRMED"
                ),
                PurchaseOrder(
                    bookId = 2L,
                    bookTitle = "الدليل الشامل للعملات الرقمية والبلوكتشين",
                    paymentMethod = "BINANCE",
                    amountFormatted = "10.00 USDT",
                    transactionReference = "TX-BINANCE-990145220",
                    customerContact = "karim.dz@outlook.fr",
                    customerNote = "تحويل USDT عبر شبكة TRC20 من باريس.",
                    timestamp = System.currentTimeMillis() - 3600000L * 2,
                    status = "CONFIRMED"
                )
            )
            purchaseOrderDao.insertAll(starterOrders)
        }

        // Initialize starter reader reviews if empty
        if (reviewDao.getReviewCount() == 0) {
            val starterReviews = listOf(
                Review(
                    bookId = 1L,
                    bookTitle = "أنوار الحكمة والمعرفة الرقمية",
                    readerName = "رياض موهوب (الجزائر العاصمة)",
                    rating = 5,
                    comment = "كتاب فكري عميق ورائع جداً! أسلوب الكاتب سلس وعميق في نفس الوقت. تم الدفع بكل سهولة عبر تطبيق بريدي موب ووصلتني النسخة فوراً.",
                    paymentMethod = "BARIDIMOB",
                    timestamp = System.currentTimeMillis() - 86400000L * 3,
                    isVerifiedPurchase = true
                ),
                Review(
                    bookId = 2L,
                    bookTitle = "الدليل الشامل للعملات الرقمية والبلوكتشين",
                    readerName = "سارة بن خليل (دبي)",
                    rating = 5,
                    comment = "أفضل مرجع عربي مبسط عن تقنية البلوكتشين وتداول العملات المشفرة. خاصية الدفع عبر Binance USDT بالغة السرعة والراحة للمقيمين بالخارج.",
                    paymentMethod = "BINANCE",
                    timestamp = System.currentTimeMillis() - 86400000L * 2,
                    isVerifiedPurchase = true
                ),
                Review(
                    bookId = 3L,
                    bookTitle = "قبسات من التراث الجزائري والأندلسي",
                    readerName = "د. فريد بولوداني (قسنطينة)",
                    rating = 5,
                    comment = "مكتبة منيرة بحق. إتاحة هذا الكتاب القيّم بالمجان هدية لا تقدر بثمن لطلبة التاريخ والباحثين في التراث الأندلسي والجزائري.",
                    paymentMethod = "DIRECT",
                    timestamp = System.currentTimeMillis() - 86400000L * 1,
                    isVerifiedPurchase = true
                ),
                Review(
                    bookId = 1L,
                    bookTitle = "أنوار الحكمة والمعرفة الرقمية",
                    readerName = "أمين قادري (وهران)",
                    rating = 5,
                    comment = "مبادرة ممتازة من المؤلف بإنشاء هذه المنصة المستقلة. دعم المؤلف مباشرة عبر بريدي موب خطوة تشكر عليها.",
                    paymentMethod = "BARIDIMOB",
                    timestamp = System.currentTimeMillis() - 3600000L * 8,
                    isVerifiedPurchase = true
                )
            )
            reviewDao.insertAll(starterReviews)
        }
    }

    suspend fun insertBook(book: Book): Long = bookDao.insertBook(book)
    suspend fun updateBook(book: Book) = bookDao.updateBook(book)
    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)
    suspend fun unlockBook(bookId: Long) = bookDao.unlockBook(bookId)
    suspend fun updateBookPrice(bookId: Long, priceDzd: Double, priceUsdt: Double, isFree: Boolean) =
        bookDao.updateBookPrice(bookId, priceDzd, priceUsdt, isFree)

    suspend fun savePaymentSettings(settings: PaymentSettings) = paymentSettingsDao.saveSettings(settings)
    suspend fun insertOrder(order: PurchaseOrder): Long = purchaseOrderDao.insertOrder(order)
    suspend fun deleteOrder(orderId: Long) = purchaseOrderDao.deleteOrder(orderId)
    suspend fun updateOrderStatus(orderId: Long, status: String) = purchaseOrderDao.updateOrderStatus(orderId, status)

    suspend fun insertReview(review: Review): Long = reviewDao.insertReview(review)
    suspend fun deleteReview(reviewId: Long) = reviewDao.deleteReview(reviewId)
}

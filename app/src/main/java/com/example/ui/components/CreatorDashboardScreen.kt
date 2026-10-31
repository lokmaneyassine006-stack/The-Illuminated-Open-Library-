package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Book
import com.example.data.model.PurchaseOrder
import com.example.data.model.Review
import com.example.ui.theme.BaridiMobGreen
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.CreatorTab
import com.example.ui.viewmodel.ReviewsSummary
import com.example.ui.viewmodel.SalesSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreatorDashboardScreen(
    books: List<Book>,
    orders: List<PurchaseOrder>,
    reviews: List<Review>,
    salesSummary: SalesSummary,
    reviewsSummary: ReviewsSummary,
    selectedTab: CreatorTab,
    onTabSelect: (CreatorTab) -> Unit,
    onBackClick: () -> Unit,
    onOpenPaymentSettings: () -> Unit,
    onToggleOrderStatus: (PurchaseOrder) -> Unit,
    onDeleteOrder: (Long) -> Unit,
    onDeleteReview: (Review) -> Unit,
    onRecordManualSale: (Book, String, String, String, String, String) -> Unit,
    onSaveReview: (Book, String, Int, String, String, Boolean) -> Unit
) {
    var showRecordSaleDialog by remember { mutableStateOf(false) }
    var showAddReviewDialog by remember { mutableStateOf(false) }
    var salesFilter by remember { mutableStateOf("ALL") } // "ALL", "BARIDIMOB", "BINANCE", "CONFIRMED"
    var reviewsFilterRating by remember { mutableIntStateOf(0) } // 0 means all ratings

    val filteredOrders = remember(orders, salesFilter) {
        when (salesFilter) {
            "BARIDIMOB" -> orders.filter { it.paymentMethod.equals("BARIDIMOB", ignoreCase = true) }
            "BINANCE" -> orders.filter { it.paymentMethod.equals("BINANCE", ignoreCase = true) }
            "CONFIRMED" -> orders.filter { it.status.equals("CONFIRMED", ignoreCase = true) }
            else -> orders
        }
    }

    val filteredReviews = remember(reviews, reviewsFilterRating) {
        if (reviewsFilterRating > 0) {
            reviews.filter { it.rating == reviewsFilterRating }
        } else {
            reviews
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("creator_dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Banner
        item {
            CreatorHeaderCard(
                onBackClick = onBackClick,
                onOpenPaymentSettings = onOpenPaymentSettings
            )
        }

        // 2. Tabs Selector (Sales vs Reviews)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                TabRow(
                    selectedTabIndex = if (selectedTab == CreatorTab.SALES) 0 else 1,
                    containerColor = Color.Transparent,
                    contentColor = GoldPrimary
                ) {
                    Tab(
                        selected = selectedTab == CreatorTab.SALES,
                        onClick = { onTabSelect(CreatorTab.SALES) },
                        modifier = Modifier.testTag("creator_tab_sales"),
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "لوحة المبيعات والإيرادات",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == CreatorTab.REVIEWS,
                        onClick = { onTabSelect(CreatorTab.REVIEWS) },
                        modifier = Modifier.testTag("creator_tab_reviews"),
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RateReview,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "آراء القراء والتقييمات",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    )
                }
            }
        }

        // 3. Tab Contents
        if (selectedTab == CreatorTab.SALES) {
            // Financial KPI Cards
            item {
                SalesKpiSection(
                    salesSummary = salesSummary,
                    onRecordSaleClick = { showRecordSaleDialog = true },
                    onPaymentSettingsClick = onOpenPaymentSettings
                )
            }

            // Sales breakdown by book
            item {
                BookSalesBreakdownCard(
                    books = books,
                    orders = orders
                )
            }

            // Filter Chips for Orders
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "سجل العمليات والطلبات الواردة:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = salesFilter == "ALL",
                                onClick = { salesFilter = "ALL" },
                                label = { Text("الكل (${orders.size})") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = salesFilter == "BARIDIMOB",
                                onClick = { salesFilter = "BARIDIMOB" },
                                label = { Text("بريدي موب 🇩🇿 (${salesSummary.baridiMobOrdersCount})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BaridiMobGreen.copy(alpha = 0.2f),
                                    selectedLabelColor = BaridiMobGreen
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = salesFilter == "BINANCE",
                                onClick = { salesFilter = "BINANCE" },
                                label = { Text("بينانس USDT 🌐 (${salesSummary.binanceOrdersCount})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BinanceYellow.copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFFC78700)
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = salesFilter == "CONFIRMED",
                                onClick = { salesFilter = "CONFIRMED" },
                                label = { Text("مؤكدة (${salesSummary.confirmedOrders})") }
                            )
                        }
                    }
                }
            }

            // Orders List
            if (filteredOrders.isEmpty()) {
                item {
                    EmptySalesCard(onRecordSale = { showRecordSaleDialog = true })
                }
            } else {
                items(filteredOrders, key = { it.orderId }) { order ->
                    CreatorOrderItemCard(
                        order = order,
                        onToggleStatus = { onToggleOrderStatus(order) },
                        onDelete = { onDeleteOrder(order.orderId) }
                    )
                }
            }
        } else {
            // Reviews & Feedback Tab
            item {
                ReviewsSummaryCard(
                    summary = reviewsSummary,
                    onAddReviewClick = { showAddReviewDialog = true }
                )
            }

            // Rating Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "تصفية آراء القراء:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = reviewsFilterRating == 0,
                                onClick = { reviewsFilterRating = 0 },
                                label = { Text("جميع التقييمات (${reviews.size})") }
                            )
                        }
                        (5 downTo 1).forEach { stars ->
                            val count = reviews.count { it.rating == stars }
                            item {
                                FilterChip(
                                    selected = reviewsFilterRating == stars,
                                    onClick = { reviewsFilterRating = if (reviewsFilterRating == stars) 0 else stars },
                                    label = { Text("$stars ★ ($count)") }
                                )
                            }
                        }
                    }
                }
            }

            // Reviews List
            if (filteredReviews.isEmpty()) {
                item {
                    EmptyReviewsCard(onAddReview = { showAddReviewDialog = true })
                }
            } else {
                items(filteredReviews, key = { it.reviewId }) { review ->
                    CreatorReviewItemCard(
                        review = review,
                        onDelete = { onDeleteReview(review) }
                    )
                }
            }
        }
    }

    // Dialog for recording a manual sale
    if (showRecordSaleDialog) {
        RecordManualSaleDialog(
            books = books,
            onDismiss = { showRecordSaleDialog = false },
            onSaveSale = { book, method, amount, ref, contact, note ->
                onRecordManualSale(book, method, amount, ref, contact, note)
                showRecordSaleDialog = false
            }
        )
    }

    // Dialog for adding reader review
    if (showAddReviewDialog) {
        AddReaderReviewDialog(
            books = books,
            onDismiss = { showAddReviewDialog = false },
            onSaveReview = { book, readerName, rating, comment, method, isVerified ->
                onSaveReview(book, readerName, rating, comment, method, isVerified)
                showAddReviewDialog = false
            }
        )
    }
}

@Composable
private fun CreatorHeaderCard(
    onBackClick: () -> Unit,
    onOpenPaymentSettings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B),
                            Color(0xFF311042)
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                OutlinedButton(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("creator_back_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع للمكتبة",
                            modifier = Modifier.size(16.dp)
                        )
                        Text("المكتبة والكتب", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Payment settings quick button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldPrimary.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldLight),
                    modifier = Modifier
                        .clickable { onOpenPaymentSettings() }
                        .testTag("creator_payment_settings_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "إعدادات الدفع",
                            color = GoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldPrimary,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = "لوحة تحكم المنشئ والمؤلف",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "The Illuminated Open Library • إدارة متكاملة للمبيعات، إيرادات بريدي موب وبينانس، ومراجعات القراء",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun SalesKpiSection(
    salesSummary: SalesSummary,
    onRecordSaleClick: () -> Unit,
    onPaymentSettingsClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Two Financial KPI Cards (DZD & USDT)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // BaridiMob DZD Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BaridiMobGreen.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "بريدي موب 🇩🇿",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaridiMobGreen
                        )
                        Surface(
                            shape = CircleShape,
                            color = BaridiMobGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("DA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BaridiMobGreen)
                            }
                        }
                    }

                    val formattedDzd = String.format(Locale.US, "%,.0f", salesSummary.totalDzd)
                    Text(
                        text = "$formattedDzd د.ج",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BaridiMobGreen.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "${salesSummary.baridiMobOrdersCount} عملية داخل الجزائر",
                            fontSize = 11.sp,
                            color = BaridiMobGreen,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Binance USDT Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BinanceYellow.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "بينانس USDT 🌐",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC78700)
                        )
                        Surface(
                            shape = CircleShape,
                            color = BinanceYellow.copy(alpha = 0.25f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("₮", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF946200))
                            }
                        }
                    }

                    val formattedUsdt = String.format(Locale.US, "%.2f", salesSummary.totalUsdt)
                    Text(
                        text = "$formattedUsdt USDT",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BinanceYellow.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${salesSummary.binanceOrdersCount} عملية من الخارج",
                            fontSize = 11.sp,
                            color = Color(0xFF946200),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Summary Performance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "إجمالي الطلبات",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${salesSummary.totalOrders} معاملة",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column {
                    Text(
                        text = "الطلبات المؤكدة",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${salesSummary.confirmedOrders} مكتملة",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BaridiMobGreen
                    )
                }

                Button(
                    onClick = onRecordSaleClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color(0xFF1F1600)
                    ),
                    modifier = Modifier.testTag("record_manual_sale_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PriceCheck,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("تسجيل مبيعة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BookSalesBreakdownCard(
    books: List<Book>,
    orders: List<PurchaseOrder>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مبيعات كتبي في المنصة:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${books.size} كتاب مسجل",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            books.forEach { book ->
                val bookOrders = orders.filter { it.bookId == book.id }
                val dzdCount = bookOrders.count { it.paymentMethod.equals("BARIDIMOB", ignoreCase = true) }
                val usdtCount = bookOrders.count { it.paymentMethod.equals("BINANCE", ignoreCase = true) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = book.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (book.isFree) "كتاب مجاني مفتوح" else "${book.priceDzd.toInt()} د.ج / ${book.priceUsdt} USDT",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BaridiMobGreen.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "🇩🇿 $dzdCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BaridiMobGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BinanceYellow.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "🌐 $usdtCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF946200),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatorOrderItemCard(
    order: PurchaseOrder,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("creator_order_item_${order.orderId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Method Badge
                val isBaridiMob = order.paymentMethod.equals("BARIDIMOB", ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isBaridiMob) BaridiMobGreen.copy(alpha = 0.15f) else BinanceYellow.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isBaridiMob) BaridiMobGreen else BinanceYellow
                    )
                ) {
                    Text(
                        text = if (isBaridiMob) "🇩🇿 بريدي موب BaridiMob" else "🌐 بينانس Binance USDT",
                        color = if (isBaridiMob) BaridiMobGreen else Color(0xFF946200),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Amount
                Text(
                    text = order.amountFormatted,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = order.bookTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            // Reference & Contact
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "رقم المعاملة / الوصل: ${order.transactionReference}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (order.customerContact.isNotBlank()) {
                        Text(
                            text = "جهة اتصال المشتري: ${order.customerContact}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (order.customerNote.isNotBlank()) {
                        Text(
                            text = "ملاحظة: ${order.customerNote}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    val formattedDate = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).format(Date(order.timestamp))
                    Text(
                        text = "تاريخ الطلب: $formattedDate",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Action row: Toggle Status & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isConfirmed = order.status.equals("CONFIRMED", ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isConfirmed) BaridiMobGreen.copy(alpha = 0.12f) else Color.Gray.copy(alpha = 0.12f),
                    modifier = Modifier.clickable { onToggleStatus() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isConfirmed) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            tint = if (isConfirmed) BaridiMobGreen else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isConfirmed) "الحالة: مؤكد ومكتمل" else "الحالة: قيد المراجعة",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isConfirmed) BaridiMobGreen else Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "حذف الطلب",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewsSummaryCard(
    summary: ReviewsSummary,
    onAddReviewClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "متوسط تقييم القراء",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.1f", summary.averageRating),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Column {
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = if (index < summary.averageRating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = "بناءً على ${summary.totalReviews} رأي ومراجعة",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Button(
                    onClick = onAddReviewClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color(0xFF1F1600)
                    ),
                    modifier = Modifier.testTag("add_reader_review_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddComment,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("إضافة رأي", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Rating Distribution Progress Bars
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                (5 downTo 1).forEach { stars ->
                    val count = summary.starCounts[stars] ?: 0
                    val fraction = if (summary.totalReviews > 0) count.toFloat() / summary.totalReviews else 0f
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$stars ★",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(28.dp)
                        )
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = GoldPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(
                            text = "$count",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(20.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatorReviewItemCard(
    review: Review,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("creator_review_item_${review.reviewId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldPrimary.copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = review.readerName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = review.readerName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            if (review.isVerifiedPurchase) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "قارئ ومشترٍ موثق",
                                    tint = BaridiMobGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        val formattedDate = SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date(review.timestamp))
                        Text(
                            text = formattedDate,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Stars display
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Book title badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "عن كتاب: ${review.bookTitle}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            // Comment text
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(
                    text = "« ${review.comment} »",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(10.dp)
                )
            }

            // Footer action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "✍️ شكر المنشئ: رأي قيّم ومقدر",
                        fontSize = 11.sp,
                        color = GoldLight,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "حذف الرأي",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySalesCard(onRecordSale: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "لا توجد عمليات مبيعات مسجلة في هذا التصنيف حالياً",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Button(
                onClick = onRecordSale,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
            ) {
                Text("تسجيل أول مبيعة الآن")
            }
        }
    }
}

@Composable
private fun EmptyReviewsCard(onAddReview: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.RateReview,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "لا توجد آراء قراء تطابق هذا التقييم حالياً",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Button(
                onClick = onAddReview,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
            ) {
                Text("إضافة رأي قارئ جديد")
            }
        }
    }
}

@Composable
private fun RecordManualSaleDialog(
    books: List<Book>,
    onDismiss: () -> Unit,
    onSaveSale: (Book, String, String, String, String, String) -> Unit
) {
    var selectedBookIndex by remember { mutableIntStateOf(0) }
    var selectedMethod by remember { mutableStateOf("BARIDIMOB") } // "BARIDIMOB" or "BINANCE"
    var amount by remember { mutableStateOf(if (books.isNotEmpty()) "${books.first().priceDzd.toInt()} د.ج" else "1200 د.ج") }
    var reference by remember { mutableStateOf("") }
    var customerContact by remember { mutableStateOf("") }
    var customerNote by remember { mutableStateOf("") }

    val currentBook = books.getOrNull(selectedBookIndex) ?: Book(title = "كتاب عام", author = "")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تسجيل مبيعة يدوية للمؤلف",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("اختر الكتاب المباع:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(books.indices.toList()) { index ->
                        val b = books[index]
                        FilterChip(
                            selected = selectedBookIndex == index,
                            onClick = {
                                selectedBookIndex = index
                                amount = if (selectedMethod == "BARIDIMOB") "${b.priceDzd.toInt()} د.ج" else "${b.priceUsdt} USDT"
                            },
                            label = { Text(b.title.take(15) + "...") }
                        )
                    }
                }

                Text("طريقة استلام الدفع:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedMethod == "BARIDIMOB",
                        onClick = {
                            selectedMethod = "BARIDIMOB"
                            amount = "${currentBook.priceDzd.toInt()} د.ج"
                        },
                        label = { Text("بريدي موب 🇩🇿") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedMethod == "BINANCE",
                        onClick = {
                            selectedMethod = "BINANCE"
                            amount = "${currentBook.priceUsdt} USDT"
                        },
                        label = { Text("بينانس 🌐") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("المبلغ المحصل") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = reference,
                    onValueChange = { reference = it },
                    label = { Text("رقم الوصل أو المعاملة (اختياري)") },
                    placeholder = { Text("مثال: BM-2026-11200") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = customerContact,
                    onValueChange = { customerContact = it },
                    label = { Text("هاتف أو إيميل المشتري (اختياري)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = customerNote,
                    onValueChange = { customerNote = it },
                    label = { Text("ملاحظات إضافية") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveSale(currentBook, selectedMethod, amount, reference, customerContact, customerNote)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                modifier = Modifier.testTag("confirm_manual_sale_btn")
            ) {
                Text("حفظ وتسجيل المبيعة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
private fun AddReaderReviewDialog(
    books: List<Book>,
    onDismiss: () -> Unit,
    onSaveReview: (Book, String, Int, String, String, Boolean) -> Unit
) {
    var selectedBookIndex by remember { mutableIntStateOf(0) }
    var readerName by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var isVerified by remember { mutableStateOf(true) }
    var paymentMethod by remember { mutableStateOf("BARIDIMOB") }

    val currentBook = books.getOrNull(selectedBookIndex) ?: Book(title = "كتاب عام", author = "")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إضافة رأي أو مراجعة قارئ",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("الكتاب المقروء:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(books.indices.toList()) { index ->
                        val b = books[index]
                        FilterChip(
                            selected = selectedBookIndex == index,
                            onClick = { selectedBookIndex = index },
                            label = { Text(b.title.take(15) + "...") }
                        )
                    }
                }

                // Interactive Star Rating Selector
                Text("التقييم بالنجوم:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { starIndex ->
                        IconButton(onClick = { rating = starIndex }) {
                            Icon(
                                imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$starIndex نجوم",
                                tint = GoldPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = readerName,
                    onValueChange = { readerName = it },
                    label = { Text("اسم القارئ") },
                    placeholder = { Text("مثال: عبد الرحمن (وهران)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("رأي القارئ وملاحظاته حول الكتاب") },
                    placeholder = { Text("اكتب انطباع القارئ، جودة المحتوى، وسرعة الدفع...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = paymentMethod == "BARIDIMOB",
                        onClick = { paymentMethod = "BARIDIMOB" },
                        label = { Text("بريدي موب 🇩🇿") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = paymentMethod == "BINANCE",
                        onClick = { paymentMethod = "BINANCE" },
                        label = { Text("بينانس 🌐") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (comment.isNotBlank()) {
                        onSaveReview(currentBook, readerName, rating, comment, paymentMethod, isVerified)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                modifier = Modifier.testTag("confirm_add_review_btn")
            ) {
                Text("نشر رأي القارئ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

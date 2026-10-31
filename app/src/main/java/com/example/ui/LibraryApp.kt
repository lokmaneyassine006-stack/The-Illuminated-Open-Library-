package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Book
import com.example.ui.components.AddEditBookDialog
import com.example.ui.components.AiBookAssistantDialog
import com.example.ui.components.BookCard
import com.example.ui.components.BookCategories
import com.example.ui.components.BookCheckoutDialog
import com.example.ui.components.BookReaderDialog
import com.example.ui.components.OrdersHistoryDialog
import com.example.ui.components.PaymentSettingsDialog
import com.example.ui.components.QuickPriceDialog
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.platform.LocalContext
import com.example.data.auth.AuthUser
import com.example.ui.components.AuthDialog
import com.example.ui.components.CreatorDashboardScreen
import com.example.ui.theme.BaridiMobGreen
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CreatorTab
import com.example.ui.viewmodel.LibraryViewModel

@Composable
fun LibraryApp(viewModel: LibraryViewModel) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val creatorSelectedTab by viewModel.creatorSelectedTab.collectAsStateWithLifecycle()
    val books by viewModel.filteredBooks.collectAsStateWithLifecycle()
    val allBooks by viewModel.allBooks.collectAsStateWithLifecycle()
    val paymentSettings by viewModel.paymentSettings.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val reviews by viewModel.allReviews.collectAsStateWithLifecycle()
    val salesSummary by viewModel.salesSummary.collectAsStateWithLifecycle()
    val reviewsSummary by viewModel.reviewsSummary.collectAsStateWithLifecycle()

    val authUser by viewModel.authUser.collectAsStateWithLifecycle()
    val isAuthDialogOpen by viewModel.isAuthDialogOpen.collectAsStateWithLifecycle()
    val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
    val authErrorMessage by viewModel.authErrorMessage.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedPriceFilter by viewModel.selectedPriceFilter.collectAsStateWithLifecycle()

    val isAddBookDialogOpen by viewModel.isAddBookDialogOpen.collectAsStateWithLifecycle()
    val isSavingBook by viewModel.isSavingBook.collectAsStateWithLifecycle()
    val isPaymentSettingsDialogOpen by viewModel.isPaymentSettingsDialogOpen.collectAsStateWithLifecycle()
    val bookToEditPrice by viewModel.bookToEditPrice.collectAsStateWithLifecycle()
    val bookToEdit by viewModel.bookToEdit.collectAsStateWithLifecycle()
    val bookForCheckout by viewModel.bookForCheckout.collectAsStateWithLifecycle()
    val bookForReading by viewModel.bookForReading.collectAsStateWithLifecycle()
    val isOrdersDialogOpen by viewModel.isOrdersDialogOpen.collectAsStateWithLifecycle()

    // Gemini AI states
    val bookForAiAssistant by viewModel.bookForAiAssistant.collectAsStateWithLifecycle()
    val aiDraftState by viewModel.aiDraftState.collectAsStateWithLifecycle()
    val aiReaderState by viewModel.aiReaderState.collectAsStateWithLifecycle()

    val snackbarMessage by viewModel.userSnackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.CATALOG,
                        onClick = { viewModel.selectScreen(AppScreen.CATALOG) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = "المكتبة والكتب"
                            )
                        },
                        label = { Text("المكتبة والكتب", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GoldLight,
                            selectedTextColor = GoldLight,
                            indicatorColor = GoldPrimary.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("nav_catalog_tab")
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.CREATOR_DASHBOARD && creatorSelectedTab == CreatorTab.SALES,
                        onClick = {
                            viewModel.selectCreatorTab(CreatorTab.SALES)
                            viewModel.selectScreen(AppScreen.CREATOR_DASHBOARD)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "لوحة المبيعات"
                            )
                        },
                        label = { Text("لوحة المبيعات", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BaridiMobGreen,
                            selectedTextColor = BaridiMobGreen,
                            indicatorColor = BaridiMobGreen.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_sales_tab")
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.CREATOR_DASHBOARD && creatorSelectedTab == CreatorTab.REVIEWS,
                        onClick = {
                            viewModel.selectCreatorTab(CreatorTab.REVIEWS)
                            viewModel.selectScreen(AppScreen.CREATOR_DASHBOARD)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = "آراء القراء"
                            )
                        },
                        label = { Text("آراء القراء", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GoldLight,
                            selectedTextColor = GoldLight,
                            indicatorColor = GoldPrimary.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("nav_reviews_tab")
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.statusBars
        ) { innerPadding ->
            if (currentScreen == AppScreen.CREATOR_DASHBOARD) {
                BackHandler {
                    viewModel.selectScreen(AppScreen.CATALOG)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CreatorDashboardScreen(
                        books = allBooks,
                        orders = orders,
                        reviews = reviews,
                        salesSummary = salesSummary,
                        reviewsSummary = reviewsSummary,
                        selectedTab = creatorSelectedTab,
                        onTabSelect = { viewModel.selectCreatorTab(it) },
                        onBackClick = { viewModel.selectScreen(AppScreen.CATALOG) },
                        onOpenPaymentSettings = { viewModel.isPaymentSettingsDialogOpen.value = true },
                        onToggleOrderStatus = { viewModel.toggleOrderStatus(it) },
                        onDeleteOrder = { viewModel.deleteOrder(it) },
                        onDeleteReview = { viewModel.deleteReview(it) },
                        onRecordManualSale = { book, method, amount, ref, contact, note ->
                            viewModel.recordManualSale(book, method, amount, ref, contact, note)
                        },
                        onSaveReview = { book, readerName, rating, comment, method, isVerified ->
                            viewModel.saveReview(book, readerName, rating, comment, method, isVerified)
                        }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Hero Header & Visual Branding
                    item {
                        HeroHeaderSection(
                            totalBooks = allBooks.size,
                            ordersCount = orders.size,
                            user = authUser,
                            onOpenOrders = { viewModel.isOrdersDialogOpen.value = true },
                            onOpenCreatorDashboard = {
                                viewModel.selectCreatorTab(CreatorTab.SALES)
                                viewModel.selectScreen(AppScreen.CREATOR_DASHBOARD)
                            },
                            onOpenAuth = { viewModel.openAuthDialog() }
                        )
                    }

                    // 2. Primary Action Buttons (Requested Features & Gemini AI & Creator Studio)
                    item {
                        PrimaryActionsSection(
                            onAddBookClick = {
                                viewModel.bookToEdit.value = null
                                viewModel.isAddBookDialogOpen.value = true
                            },
                            onPaymentSettingsClick = {
                                viewModel.isPaymentSettingsDialogOpen.value = true
                            },
                            onSetPriceClick = {
                                if (allBooks.isNotEmpty()) {
                                    viewModel.bookToEditPrice.value = allBooks.first()
                                } else {
                                    viewModel.userSnackbarMessage.value = "يرجى إضافة كتاب أولاً لتحديد سعره."
                                }
                            },
                            onAiAssistantClick = {
                                if (allBooks.isNotEmpty()) {
                                    viewModel.bookForAiAssistant.value = allBooks.first()
                                } else {
                                    viewModel.userSnackbarMessage.value = "يرجى إضافة كتاب أولاً لاستخدام مساعد Gemini الذكي."
                                }
                            },
                            onOpenCreatorDashboard = {
                                viewModel.selectCreatorTab(CreatorTab.SALES)
                                viewModel.selectScreen(AppScreen.CREATOR_DASHBOARD)
                            }
                        )
                    }

                    // 3. Search and Category Filters
                    item {
                        SearchAndFilterSection(
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.searchQuery.value = it },
                            selectedCategory = selectedCategory,
                            onCategorySelect = { viewModel.selectedCategory.value = it },
                            selectedPriceFilter = selectedPriceFilter,
                            onPriceFilterSelect = { viewModel.selectedPriceFilter.value = it }
                        )
                    }

                    // 4. Books Catalog Grid / List
                    if (books.isEmpty()) {
                        item {
                            EmptyBooksSection(
                                onAddBookClick = {
                                    viewModel.bookToEdit.value = null
                                    viewModel.isAddBookDialogOpen.value = true
                                }
                            )
                        }
                    } else {
                        items(books, key = { it.id }) { book ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                BookCard(
                                    book = book,
                                    onReadClick = { viewModel.bookForReading.value = book },
                                    onBuyClick = { viewModel.bookForCheckout.value = book },
                                    onEditPriceClick = { viewModel.bookToEditPrice.value = book },
                                    onEditBookClick = {
                                        viewModel.bookToEdit.value = book
                                        viewModel.isAddBookDialogOpen.value = true
                                    },
                                    onDeleteClick = { viewModel.deleteBook(book) },
                                    onAiAssistantClick = { viewModel.bookForAiAssistant.value = book }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Modals & Dialogs
        if (isAddBookDialogOpen) {
            AddEditBookDialog(
                initialBook = bookToEdit,
                authUser = authUser,
                isSaving = isSavingBook,
                aiDraftState = aiDraftState,
                onGenerateWithAi = { viewModel.generateBookDraftWithAi(it) },
                onResetAiDraftState = { viewModel.resetAiDraftState() },
                onOpenAuth = { viewModel.openAuthDialog() },
                onDismiss = {
                    viewModel.isAddBookDialogOpen.value = false
                    viewModel.bookToEdit.value = null
                    viewModel.resetAiDraftState()
                },
                onSave = { viewModel.saveBook(it) }
            )
        }

        if (isPaymentSettingsDialogOpen) {
            PaymentSettingsDialog(
                currentSettings = paymentSettings,
                onDismiss = { viewModel.isPaymentSettingsDialogOpen.value = false },
                onSave = { viewModel.savePaymentSettings(it) }
            )
        }

        bookToEditPrice?.let { book ->
            QuickPriceDialog(
                book = book,
                onDismiss = { viewModel.bookToEditPrice.value = null },
                onSavePrice = { id, dzd, usdt, isFree ->
                    viewModel.updateBookPricing(id, dzd, usdt, isFree)
                }
            )
        }

        bookForCheckout?.let { book ->
            BookCheckoutDialog(
                book = book,
                paymentSettings = paymentSettings,
                onDismiss = { viewModel.bookForCheckout.value = null },
                onConfirmPayment = { method, amount, ref, contact, note ->
                    viewModel.submitPurchaseOrder(book, method, amount, ref, contact, note)
                }
            )
        }

        bookForReading?.let { book ->
            BookReaderDialog(
                book = book,
                onDismiss = { viewModel.bookForReading.value = null },
                onBuyFullVersion = {
                    viewModel.bookForCheckout.value = book
                },
                onAskAi = {
                    viewModel.bookForAiAssistant.value = book
                }
            )
        }

        bookForAiAssistant?.let { book ->
            AiBookAssistantDialog(
                book = book,
                aiState = aiReaderState,
                onDismiss = {
                    viewModel.bookForAiAssistant.value = null
                    viewModel.resetAiReaderState()
                },
                onAskQuestion = { question ->
                    viewModel.askGeminiAboutBook(book, question)
                },
                onResetState = {
                    viewModel.resetAiReaderState()
                }
            )
        }

        if (isOrdersDialogOpen) {
            OrdersHistoryDialog(
                orders = orders,
                onDismiss = { viewModel.isOrdersDialogOpen.value = false },
                onDeleteOrder = { viewModel.deleteOrder(it) }
            )
        }

        if (isAuthDialogOpen) {
            AuthDialog(
                user = authUser,
                isLoading = isAuthLoading,
                errorMessage = authErrorMessage,
                onDismiss = { viewModel.closeAuthDialog() },
                onSignIn = { activity -> viewModel.signInWithGoogle(activity) },
                onSignOut = { viewModel.signOut(context) }
            )
        }
    }
}

@Composable
private fun HeroHeaderSection(
    totalBooks: Int,
    ordersCount: Int,
    user: AuthUser?,
    onOpenOrders: () -> Unit,
    onOpenCreatorDashboard: () -> Unit,
    onOpenAuth: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F172A),
                                Color(0xFF1E293B),
                                Color(0xFF2E1065)
                            )
                        )
                    )
            ) {
                // Background artistic glow
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldPrimary.copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "المكتبة الرقمية المفتوحة",
                                    color = GoldLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Right header action buttons (Creator Studio, Orders & Google Auth)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Creator Dashboard Quick Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldPrimary.copy(alpha = 0.35f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldLight),
                                modifier = Modifier
                                    .clickable { onOpenCreatorDashboard() }
                                    .testTag("creator_studio_header_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = "لوحة المنشئ",
                                        tint = GoldLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "لوحة المنشئ",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Orders history button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .clickable { onOpenOrders() }
                                    .testTag("orders_history_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = GoldLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "الطلبات ($ordersCount)",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Google Account / Sign-In Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (user != null) GoldPrimary.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.4f),
                                border = if (user != null) androidx.compose.foundation.BorderStroke(1.dp, GoldLight) else null,
                                modifier = Modifier
                                    .clickable { onOpenAuth() }
                                    .testTag("auth_header_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "حساب المستخدم",
                                        tint = if (user != null) GoldLight else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (user != null) (user.displayName?.take(8) ?: "حسابي") else "دخول Google",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Column {
                        Text(
                            text = "The Illuminated Open Library",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "منصة كتبي المفتوحة مع بوابات الدفع BaridiMob و Binance",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Quick Badges for Algerian & International buyers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BaridiMobGreen.copy(alpha = 0.1f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🇩🇿", fontSize = 16.sp)
                        Column {
                            Text("داخل الجزائر", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("بريدي موب BaridiMob", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaridiMobGreen)
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BinanceYellow.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🌐", fontSize = 16.sp)
                        Column {
                            Text("خارج الجزائر", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("منصة Binance USDT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BinanceYellow)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimaryActionsSection(
    onAddBookClick: () -> Unit,
    onPaymentSettingsClick: () -> Unit,
    onSetPriceClick: () -> Unit,
    onAiAssistantClick: () -> Unit,
    onOpenCreatorDashboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "التحكم السريع بالمكتبة:",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Creator Studio Highlight Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenCreatorDashboard() }
                .testTag("main_creator_dashboard_banner")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldPrimary,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "صفحة منشئ المكتبة • لوحة المبيعات وآراء القراء",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "متابعة إيرادات بريدي موب وبينانس، مبيعات الكتب، ومراجعات القراء",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldPrimary,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "دخول اللوحة",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Three Main Requested Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. زر إضافة كتابي
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onAddBookClick() }
                    .testTag("main_add_book_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GoldPrimary.copy(alpha = 0.15f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldPrimary,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LibraryAdd,
                                contentDescription = null,
                                tint = Color(0xFF1A1500),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "زر إضافة كتابي",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // 2. زر لتحديد طريقة الدفع
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPaymentSettingsClick() }
                    .testTag("main_payment_settings_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BaridiMobGreen.copy(alpha = 0.12f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, BaridiMobGreen.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BaridiMobGreen,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "طرق الدفع",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // 3. زر لوضع سعر الكتاب
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSetPriceClick() }
                    .testTag("main_set_price_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BinanceYellow.copy(alpha = 0.14f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, BinanceYellow.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BinanceYellow,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PriceChange,
                                contentDescription = null,
                                tint = Color(0xFF1E2026),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "تسعير الكتب",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 4. Gemini AI Assistant Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = GoldPrimary.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAiAssistantClick() }
                .testTag("main_ai_assistant_banner")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldPrimary,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF1A1500),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "مساعد Gemini الذكي (3.8 Flash)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldPrimary.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "Gemini API",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "اضغط لتلخيص الكتب وتحليلها والإجابة عن أي سؤال فورياً",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchAndFilterSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    selectedPriceFilter: String,
    onPriceFilterSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("ابحث عن كتاب، كاتب، أو موضوع...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary)
            },
            trailingIcon = if (searchQuery.isNotBlank()) {
                {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "مسح")
                    }
                }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_input"),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        // Price Filter Chips (الكل، مجاني، مدفوع)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("الكل", "مجاني", "مدفوع").forEach { filter ->
                FilterChip(
                    selected = selectedPriceFilter == filter,
                    onClick = { onPriceFilterSelect(filter) },
                    label = { Text(filter, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = Color(0xFF1A1500)
                    )
                )
            }
        }

        // Category Horizontal Scroll
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == "الكل",
                    onClick = { onCategorySelect("الكل") },
                    label = { Text("جميع التصنيفات", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
            items(BookCategories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onCategorySelect(cat) },
                    label = { Text(cat, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun EmptyBooksSection(onAddBookClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(60.dp)
            )
            Text(
                text = "لا توجد كتب تطابق هذا البحث",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "يمكنك إضافة كتب جديدة لمكتبتك وتحديد فترات نشرها وأسعارها الآن.",
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onAddBookClick,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF1A1500)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة كتاب الآن")
            }
        }
    }
}

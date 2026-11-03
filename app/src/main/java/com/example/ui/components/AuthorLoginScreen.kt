package com.example.ui.components

import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.auth.AuthUser
import com.example.data.model.Book
import com.example.ui.theme.BaridiMobGreen
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.SalesSummary
import com.example.util.AppTimezone
import com.example.util.TimezoneHelper
import java.util.Locale

/**
 * Dedicated Author Login and Secure Book Collection Management Screen.
 * Uses Firebase Auth and Google Credential Manager to authenticate authors,
 * protect intellectual property, manage authored collections, and configure payments.
 */
@Composable
fun AuthorLoginScreen(
    user: AuthUser?,
    isLoading: Boolean,
    errorMessage: String?,
    allBooks: List<Book>,
    salesSummary: SalesSummary,
    timezone: AppTimezone = AppTimezone.ALGIERS,
    onSignIn: (Activity) -> Unit,
    onSignOut: () -> Unit,
    onBackClick: () -> Unit,
    onOpenAddBook: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenPaymentSettings: () -> Unit,
    onOpenTimezoneSelector: () -> Unit = {},
    onEditPriceClick: (Book) -> Unit = {},
    onEditBookClick: (Book) -> Unit = {},
    onDeleteBookClick: (Book) -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity

    BackHandler {
        onBackClick()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("author_login_screen"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Bar with Back and Timezone
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("login_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع للمكتبة"
                            )
                        }

                        Text(
                            text = if (user != null) "لوحة إدارة مجموعة كتبي" else "تسجيل دخول المؤلفين",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Timezone Quick Toggle
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GoldPrimary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clickable { onOpenTimezoneSelector() }
                            .testTag("login_timezone_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(timezone.flag, fontSize = 14.sp)
                            Text(
                                text = timezone.offsetLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                    }
                }
            }
        }

        // 2. Main Content based on Authentication State
        if (user == null) {
            // Unauthenticated: Show Brand Hero & Google Sign-In Card
            item {
                UnauthenticatedAuthorHero(
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onSignIn = {
                        activity?.let { onSignIn(it) }
                    },
                    onGuestBrowse = onBackClick
                )
            }

            // Security & Benefits Cards
            item {
                AuthorPerksSection()
            }
        } else {
            // Authenticated: Author Profile Header & Metrics
            item {
                AuthenticatedAuthorHero(
                    user = user,
                    booksCount = allBooks.size,
                    salesSummary = salesSummary,
                    isLoading = isLoading,
                    onSignOut = onSignOut,
                    onOpenAddBook = onOpenAddBook,
                    onOpenDashboard = onOpenDashboard,
                    onOpenPaymentSettings = onOpenPaymentSettings
                )
            }

            // Collection Management Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "مجموعة كتبي المعروضة (${allBooks.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "إدارة الأسعار بالدينار (BaridiMob) والدولار (Binance) والتعديل",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onOpenAddBook,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("author_add_book_quick_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة كتاب", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Book Cards in Collection
            if (allBooks.isEmpty()) {
                item {
                    EmptyCollectionCard(onAddFirstBook = onOpenAddBook)
                }
            } else {
                items(allBooks, key = { it.id }) { book ->
                    AuthorBookItemCard(
                        book = book,
                        timezone = timezone,
                        onEditPrice = { onEditPriceClick(book) },
                        onEditBook = { onEditBookClick(book) },
                        onDeleteBook = { onDeleteBookClick(book) }
                    )
                }
            }
        }
    }
}

/**
 * Unauthenticated Author Hero Card with Google Sign-In with CredentialManager.
 */
@Composable
private fun UnauthenticatedAuthorHero(
    isLoading: Boolean,
    errorMessage: String?,
    onSignIn: () -> Unit,
    onGuestBrowse: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Golden library badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(GoldLight, Color(0xFF1E293B))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GoldPrimary.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "بوابة المؤلفين والناشرين الموثقة",
                    color = GoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The Illuminated Open Library",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "سجّل دخولك بحساب Google لتأمين ملكية كتبك، استقبال المدفوعات محلياً عبر BaridiMob ودولياً عبر Binance، وإدارة كامل مجموعتك المعروضة.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            // Error display
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Google Sign-In Button
            Button(
                onClick = onSignIn,
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("login_screen_google_btn")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "جارٍ التحقق والمصادقة...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "G",
                                    color = Color(0xFF4285F4),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "المتابعة بحساب Google",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Guest Browse Option
            OutlinedButton(
                onClick = onGuestBrowse,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("login_guest_browse_btn")
            ) {
                Text(
                    text = "تصفح المكتبة كزائر / قارئ",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "مصادقة آمنة ومشفرة عبر Firebase Auth و Google Credential Manager",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Explanatory feature cards highlighting author protections.
 */
@Composable
private fun AuthorPerksSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "مزايا تسجيل الدخول كمؤلف:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        PerkRow(
            icon = Icons.Default.Security,
            iconTint = GoldLight,
            title = "حماية الملكية وإدارة المجموعة",
            description = "ربط كتبك برقم معرفك (UID) المشفر لمنع التعديل غير المصرح به."
        )

        PerkRow(
            icon = Icons.Default.Payments,
            iconTint = BaridiMobGreen,
            title = "تحصيل المبيعات محلياً ودولياً",
            description = "استقبال التحويلات بالدينار الجزائري (BaridiMob) والدولار الرقمي (Binance USDT)."
        )

        PerkRow(
            icon = Icons.Default.TrendingUp,
            iconTint = BinanceYellow,
            title = "لوحة مبيعات وآراء القراء",
            description = "متابعة الطلبات، تأكيد استلام المدفوعات، وقراءة تقييمات ومراجعات القراء الموثقة."
        )
    }
}

@Composable
private fun PerkRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = iconTint.copy(alpha = 0.18f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/**
 * Authenticated Author Header showing user info, quick metrics, and author tools.
 */
@Composable
private fun AuthenticatedAuthorHero(
    user: AuthUser,
    booksCount: Int,
    salesSummary: SalesSummary,
    isLoading: Boolean,
    onSignOut: () -> Unit,
    onOpenAddBook: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenPaymentSettings: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("author_profile_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!user.photoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = "صورة المؤلف",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = GoldPrimary.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (user.displayName ?: "م").take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = GoldLight
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = user.displayName ?: "مؤلف المكتبة المنيرة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF2E7D32).copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(12.dp))
                                Text("مؤلف موثق", color = Color(0xFF4CAF50), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (!user.email.isNullOrEmpty()) {
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "معرف المؤلف: ${user.uid.take(12)}...",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary
                    )
                }

                // Sign Out
                IconButton(
                    onClick = onSignOut,
                    enabled = !isLoading,
                    modifier = Modifier.testTag("author_sign_out_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "تسجيل الخروج",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Author Quick Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBadge(
                    title = "مجموع الكتب",
                    value = "$booksCount كتاب",
                    icon = Icons.Default.AutoStories,
                    color = GoldLight,
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    title = "مبيعات بريدي موب",
                    value = String.format(Locale.US, "%.0f د.ج", salesSummary.totalDzd),
                    icon = Icons.Default.Payments,
                    color = BaridiMobGreen,
                    modifier = Modifier.weight(1.3f)
                )
                StatBadge(
                    title = "مبيعات بينانس",
                    value = String.format(Locale.US, "%.1f USDT", salesSummary.totalUsdt),
                    icon = Icons.Default.TrendingUp,
                    color = BinanceYellow,
                    modifier = Modifier.weight(1.3f)
                )
            }

            // Quick Tool Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenAddBook,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("author_add_book_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة كتاب", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onOpenDashboard,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("author_dashboard_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("لوحة المبيعات", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onOpenPaymentSettings,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("author_payment_settings_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("بيانات الدفع", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun StatBadge(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

/**
 * Book Card within the Author's collection list.
 */
@Composable
private fun AuthorBookItemCard(
    book: Book,
    timezone: AppTimezone,
    onEditPrice: () -> Unit,
    onEditBook: () -> Unit,
    onDeleteBook: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("author_book_card_${book.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Book Cover
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(60.dp, 80.dp)
            ) {
                if (book.coverImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = book.coverImageUrl,
                        contentDescription = book.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${book.author} • ${book.category}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Dual Price Badges
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (book.isFree) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF4CAF50).copy(alpha = 0.2f)) {
                            Text("مجاني", color = Color(0xFF4CAF50), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp, 2.dp))
                        }
                    } else {
                        Surface(shape = RoundedCornerShape(4.dp), color = BaridiMobGreen.copy(alpha = 0.15f)) {
                            Text(String.format(Locale.US, "%.0f د.ج", book.priceDzd), color = BaridiMobGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp, 2.dp))
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = BinanceYellow.copy(alpha = 0.15f)) {
                            Text(String.format(Locale.US, "%.1f USDT", book.priceUsdt), color = BinanceYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp, 2.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "فترة النشر: ${book.publishedPeriod}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onEditPrice,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("author_edit_price_btn_${book.id}")
                ) {
                    Icon(Icons.Default.PriceChange, contentDescription = "تعديل السعر", tint = GoldLight, modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = onEditBook,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("author_edit_book_btn_${book.id}")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "تعديل الكتاب", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = onDeleteBook,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("author_delete_book_btn_${book.id}")
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "حذف الكتاب", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyCollectionCard(onAddFirstBook: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.AutoStories, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(40.dp))
            Text(
                text = "لم تقم بإضافة كتب لمجموعتك بعد",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "ابدأ الآن بنشر أول كتاب وتحديد سعره بالدينار الجزائري والدولار الرقمي.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onAddFirstBook,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("إضافة أول كتاب الآن", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.auth.AuthUser
import com.example.data.model.Book
import com.example.ui.theme.BaridiMobGreen
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.AiDraftState
import com.example.util.AppTimezone
import com.example.util.TimezoneHelper
import kotlinx.coroutines.launch

/**
 * Supported payment method options for new book additions.
 */
enum class BookPaymentMethodOption(
    val id: String,
    val titleArabic: String,
    val subtitle: String,
    val currencyCode: String,
    val currencySymbol: String,
    val icon: ImageVector,
    val accentColor: Color
) {
    BARIDIMOB(
        id = "BARIDIMOB",
        titleArabic = "بريدي موب (BaridiMob)",
        subtitle = "حساب CCP / RIP - دينار جزائري (DZD)",
        currencyCode = "DZD",
        currencySymbol = "د.ج",
        icon = Icons.Default.AccountBalance,
        accentColor = BaridiMobGreen
    ),
    BINANCE(
        id = "BINANCE",
        titleArabic = "بينانس (Binance Pay)",
        subtitle = "محفظة رقمية USDT TRC20 / Pay ID",
        currencyCode = "USDT",
        currencySymbol = "USDT",
        icon = Icons.Default.CurrencyBitcoin,
        accentColor = BinanceYellow
    ),
    BOTH(
        id = "BOTH",
        titleArabic = "كلاهما (BaridiMob & Binance)",
        subtitle = "قبول محلي بالدينار ودولي بالدولار المشفر",
        currencyCode = "DZD / USDT",
        currencySymbol = "متعدد",
        icon = Icons.Default.Payments,
        accentColor = GoldPrimary
    )
}

/**
 * Production-grade ModalBottomSheet component to add a new book to the library,
 * featuring fields for Title, Author, Description, Price, and Payment Method (BaridiMob or Binance).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookBottomSheet(
    initialBook: Book? = null,
    authUser: AuthUser? = null,
    isSaving: Boolean = false,
    aiDraftState: AiDraftState = AiDraftState.Idle,
    timezone: AppTimezone = AppTimezone.ALGIERS,
    onGenerateWithAi: (String) -> Unit = {},
    onDismiss: () -> Unit,
    onSave: (Book) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 44.dp, height = 4.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            ) {}
        },
        modifier = Modifier.testTag("add_book_bottom_sheet")
    ) {
        AddBookFormContent(
            initialBook = initialBook,
            authUser = authUser,
            isSaving = isSaving,
            aiDraftState = aiDraftState,
            timezone = timezone,
            isBottomSheet = true,
            onGenerateWithAi = onGenerateWithAi,
            onDismiss = {
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    onDismiss()
                }
            },
            onSave = { book ->
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    onSave(book)
                }
            }
        )
    }
}

/**
 * Dialog variation of the Add Book form, sharing the exact same UI fields and state logic.
 */
@Composable
fun AddBookDialog(
    initialBook: Book? = null,
    authUser: AuthUser? = null,
    isSaving: Boolean = false,
    aiDraftState: AiDraftState = AiDraftState.Idle,
    timezone: AppTimezone = AppTimezone.ALGIERS,
    onGenerateWithAi: (String) -> Unit = {},
    onDismiss: () -> Unit,
    onSave: (Book) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .heightIn(max = 760.dp)
                .testTag("add_book_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            AddBookFormContent(
                initialBook = initialBook,
                authUser = authUser,
                isSaving = isSaving,
                aiDraftState = aiDraftState,
                timezone = timezone,
                isBottomSheet = false,
                onGenerateWithAi = onGenerateWithAi,
                onDismiss = onDismiss,
                onSave = onSave
            )
        }
    }
}

/**
 * Reusable Add Book Form Content containing all required fields:
 * - Title
 * - Author
 * - Description
 * - Price
 * - Payment Method (BaridiMob or Binance)
 */
@Composable
fun AddBookFormContent(
    initialBook: Book? = null,
    authUser: AuthUser? = null,
    isSaving: Boolean = false,
    aiDraftState: AiDraftState = AiDraftState.Idle,
    timezone: AppTimezone = AppTimezone.ALGIERS,
    isBottomSheet: Boolean = true,
    onGenerateWithAi: (String) -> Unit = {},
    onDismiss: () -> Unit,
    onSave: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    // State values
    var title by remember { mutableStateOf(initialBook?.title ?: "") }
    var author by remember { mutableStateOf(initialBook?.author ?: (authUser?.displayName ?: "الكاتب الجزائري")) }
    var description by remember { mutableStateOf(initialBook?.description ?: "") }
    var isFree by remember { mutableStateOf(initialBook?.isFree ?: false) }
    var category by remember { mutableStateOf(initialBook?.category ?: "فكر وفلسفة") }

    // Payment Method selection: BaridiMob, Binance, or Both
    var selectedPaymentMethod by remember {
        mutableStateOf(
            when {
                initialBook == null -> BookPaymentMethodOption.BARIDIMOB
                initialBook.priceDzd > 0 && initialBook.priceUsdt > 0 -> BookPaymentMethodOption.BOTH
                initialBook.priceUsdt > 0 -> BookPaymentMethodOption.BINANCE
                else -> BookPaymentMethodOption.BARIDIMOB
            }
        )
    }

    // Dynamic Price input matching the selected payment method
    var priceInputText by remember {
        mutableStateOf(
            if (initialBook != null && !initialBook.isFree) {
                if (selectedPaymentMethod == BookPaymentMethodOption.BINANCE) {
                    initialBook.priceUsdt.toString()
                } else {
                    initialBook.priceDzd.toInt().toString()
                }
            } else {
                "1200"
            }
        )
    }

    // Form errors
    var titleError by remember { mutableStateOf<String?>(null) }
    var authorError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .navigationBarsPadding()
            .testTag("add_book_form_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Form Header ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = if (initialBook == null) "إضافة كتاب جديد للمكتبة" else "تعديل بيانات الكتاب",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isBottomSheet) "ورقة بيانات الكتاب وطرق الدفع" else "نموذج تسجيل الكتاب",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("add_book_cancel_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // --- 1. Title Field ---
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "عنوان الكتاب *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) titleError = null
                },
                placeholder = { Text("مثال: في ظلال المعرفة الجزائرية") },
                leadingIcon = {
                    Icon(Icons.Default.Title, contentDescription = null, tint = GoldPrimary)
                },
                isError = titleError != null,
                supportingText = titleError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_book_title_field")
                    .testTag("author_form_title_input")
            )
        }

        // --- 2. Author Field ---
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "اسم المؤلف *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = author,
                onValueChange = {
                    author = it
                    if (it.isNotBlank()) authorError = null
                },
                placeholder = { Text("اسم الكاتب أو صاحب العمل") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary)
                },
                isError = authorError != null,
                supportingText = authorError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_book_author_field")
                    .testTag("author_form_author_input")
            )
        }

        // --- 3. Description Field ---
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "وصف ونبذة عن الكتاب",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("اكتب ملخصاً شيقاً عن محتوى الكتاب، أفكاره الرئيسية، والجمهور المستهدف...") },
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null, tint = GoldPrimary)
                },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_book_description_field")
                    .testTag("author_form_desc_input")
            )
        }

        // Gemini AI Draft Assistant (Quick Autofill helper)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = GoldPrimary.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "اقتراح وصف وتسعير ذكي بـ Gemini AI",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedButton(
                    onClick = { onGenerateWithAi(title.ifBlank { "كتاب في الأدب والفكر" }) },
                    enabled = aiDraftState !is AiDraftState.Loading,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    if (aiDraftState is AiDraftState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                    } else {
                        Text("✨ توليد تلقائي", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // --- 4. Payment Method Selection (BaridiMob or Binance) ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "طريقة الدفع المعتمدة للكتاب *",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Text(
                    text = "حدد وسيلة التحويل المفضلة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Payment Options Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_book_payment_method_selector"),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // BaridiMob Option
                PaymentMethodSelectionCard(
                    option = BookPaymentMethodOption.BARIDIMOB,
                    isSelected = selectedPaymentMethod == BookPaymentMethodOption.BARIDIMOB,
                    onSelect = {
                        selectedPaymentMethod = BookPaymentMethodOption.BARIDIMOB
                        if (priceInputText.toDoubleOrNull() != null && priceInputText.toDoubleOrNull()!! < 50.0) {
                            priceInputText = "1200"
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_book_payment_method_baridimob")
                )

                // Binance Option
                PaymentMethodSelectionCard(
                    option = BookPaymentMethodOption.BINANCE,
                    isSelected = selectedPaymentMethod == BookPaymentMethodOption.BINANCE,
                    onSelect = {
                        selectedPaymentMethod = BookPaymentMethodOption.BINANCE
                        if (priceInputText.toDoubleOrNull() != null && priceInputText.toDoubleOrNull()!! > 200.0) {
                            priceInputText = "6.0"
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_book_payment_method_binance")
                )

                // Both Option
                PaymentMethodSelectionCard(
                    option = BookPaymentMethodOption.BOTH,
                    isSelected = selectedPaymentMethod == BookPaymentMethodOption.BOTH,
                    onSelect = {
                        selectedPaymentMethod = BookPaymentMethodOption.BOTH
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_book_payment_method_both")
                )
            }

            // Selected Payment Method Descriptive Hint
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = selectedPaymentMethod.accentColor.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, selectedPaymentMethod.accentColor.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = selectedPaymentMethod.icon,
                        contentDescription = null,
                        tint = selectedPaymentMethod.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = when (selectedPaymentMethod) {
                            BookPaymentMethodOption.BARIDIMOB ->
                                "🇩🇿 تحويل فوري محلي بالدينار الجزائري عبر تطبيق بريدي موب (RIP / CCP) لحساب البريد الجزائري."
                            BookPaymentMethodOption.BINANCE ->
                                "🌐 تحويل رقمي دولي عبر Binance Pay وUSDT (TRC20/BEP20) بدون قيود مصرفية."
                            BookPaymentMethodOption.BOTH ->
                                "✨ دعم مزدوج: متاح للشراء بالدينار الجزائري محلياً وبالدولار المشفر USDT للمغتربين والدوليين."
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // --- 5. Price & Free/Paid Toggle ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سعر الكتاب *",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isFree) "كتاب مجاني للجميع" else "كتاب مدفوع",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFree) BaridiMobGreen else GoldPrimary
                    )
                    Switch(
                        checked = !isFree,
                        onCheckedChange = { isFree = !it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GoldPrimary,
                            checkedTrackColor = GoldPrimary.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("add_book_is_free_switch")
                    )
                }
            }

            AnimatedVisibility(visible = !isFree) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = priceInputText,
                        onValueChange = {
                            priceInputText = it
                            if (it.isNotBlank()) priceError = null
                        },
                        label = {
                            Text(
                                text = when (selectedPaymentMethod) {
                                    BookPaymentMethodOption.BARIDIMOB -> "السعر بالدينار الجزائري (DZD)"
                                    BookPaymentMethodOption.BINANCE -> "السعر بالدولار المشفر (USDT)"
                                    BookPaymentMethodOption.BOTH -> "السعر الأساسي (DZD)"
                                }
                            )
                        },
                        placeholder = {
                            Text(if (selectedPaymentMethod == BookPaymentMethodOption.BINANCE) "6.0" else "1200")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = selectedPaymentMethod.icon,
                                contentDescription = null,
                                tint = selectedPaymentMethod.accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = selectedPaymentMethod.currencySymbol,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedPaymentMethod.accentColor,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        },
                        isError = priceError != null,
                        supportingText = priceError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (selectedPaymentMethod == BookPaymentMethodOption.BINANCE)
                                KeyboardType.Decimal else KeyboardType.Number
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_book_price_field")
                            .testTag("author_form_price_dzd_input")
                    )

                    // Equivalent currency conversion preview
                    val parsedPrice = priceInputText.toDoubleOrNull() ?: 0.0
                    if (parsedPrice > 0.0) {
                        val equivalentText = when (selectedPaymentMethod) {
                            BookPaymentMethodOption.BARIDIMOB ->
                                "يعادل تقريباً ${(parsedPrice / 200.0).formatDecimals(2)} USDT في بينانس"
                            BookPaymentMethodOption.BINANCE ->
                                "يعادل تقريباً ${(parsedPrice * 200.0).toInt()} د.ج عبر بريدي موب"
                            BookPaymentMethodOption.BOTH ->
                                "يعادل ${(parsedPrice / 200.0).formatDecimals(2)} USDT دولياً"
                        }
                        Text(
                            text = "💱 $equivalentText (سعر الصرف التقديري: 1 USDT = 200 د.ج)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // --- Action Buttons ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("إلغاء")
            }

            Button(
                onClick = {
                    // Validation
                    if (title.isBlank()) {
                        titleError = "يرجى كتابة عنوان الكتاب"
                        return@Button
                    }
                    if (author.isBlank()) {
                        authorError = "يرجى كتابة اسم المؤلف"
                        return@Button
                    }

                    val rawPrice = priceInputText.toDoubleOrNull() ?: 0.0
                    val (finalDzd, finalUsdt) = when (selectedPaymentMethod) {
                        BookPaymentMethodOption.BARIDIMOB -> Pair(rawPrice, (rawPrice / 200.0).coerceAtLeast(1.0))
                        BookPaymentMethodOption.BINANCE -> Pair((rawPrice * 200.0), rawPrice)
                        BookPaymentMethodOption.BOTH -> Pair(rawPrice, (rawPrice / 200.0).coerceAtLeast(1.0))
                    }

                    val newBook = (initialBook ?: Book(title = title)).copy(
                        title = title.trim(),
                        author = author.trim(),
                        description = description.trim(),
                        category = category,
                        isFree = isFree,
                        priceDzd = if (isFree) 0.0 else finalDzd,
                        priceUsdt = if (isFree) 0.0 else finalUsdt,
                        publishedPeriod = initialBook?.publishedPeriod ?: "2026"
                    )

                    onSave(newBook)
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1.5f)
                    .height(48.dp)
                    .testTag("add_book_submit_button")
                    .testTag("author_form_save_btn")
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("جاري الحفظ...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (initialBook == null) "إضافة الكتاب للمكتبة" else "حفظ التعديلات",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Individual selectable payment method card (BaridiMob, Binance, Both).
 */
@Composable
private fun PaymentMethodSelectionCard(
    option: BookPaymentMethodOption,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) option.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
        label = "borderColor"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) option.accentColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        label = "containerColor"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() }
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    shape = CircleShape,
                    color = option.accentColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            tint = option.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = when (option) {
                    BookPaymentMethodOption.BARIDIMOB -> "بريدي موب"
                    BookPaymentMethodOption.BINANCE -> "بينانس"
                    BookPaymentMethodOption.BOTH -> "كلاهما"
                },
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) option.accentColor else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = option.currencySymbol,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun Double.formatDecimals(digits: Int): String {
    return String.format(java.util.Locale.US, "%.${digits}f", this)
}

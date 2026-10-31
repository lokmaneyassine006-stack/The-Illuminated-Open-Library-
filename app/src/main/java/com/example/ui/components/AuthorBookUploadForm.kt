package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.auth.AuthUser
import com.example.data.model.Book
import com.example.ui.theme.BaridiMobGreen
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.CoverGradients
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.AiDraftState

val BookCategories = listOf(
    "فكر وفلسفة",
    "أدب وروايات",
    "تقنية ومال",
    "تاريخ وتراث",
    "تنمية وريادة",
    "علوم ومعارف",
    "أخرى"
)

val PresetCoverOptions = listOf(
    Pair("ذهبي منير", 0),
    Pair("زمردي أندلسي", 1),
    Pair("أزرق ملكي", 2),
    Pair("شفق ساحر", 3),
    Pair("بنفسجي صوفي", 4)
)

/**
 * Reusable, production-grade form component for authors to upload and edit book details:
 * Title, Author name, Description, Cover image (Photo Picker, URL, presets),
 * and dual Pricing (BaridiMob DZD & Binance USDT) with direct synchronization to Firebase Firestore.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthorBookUploadForm(
    initialBook: Book? = null,
    authUser: AuthUser? = null,
    isSaving: Boolean = false,
    aiDraftState: AiDraftState = AiDraftState.Idle,
    onGenerateWithAi: (String) -> Unit = {},
    onResetAiDraftState: () -> Unit = {},
    onOpenAuth: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onSaveBookToFirestore: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    // Form fields
    var title by remember { mutableStateOf(initialBook?.title ?: "") }
    var author by remember { mutableStateOf(initialBook?.author ?: (authUser?.displayName ?: "المؤلف")) }
    var category by remember { mutableStateOf(initialBook?.category ?: BookCategories.first()) }
    var publishedPeriod by remember { mutableStateOf(initialBook?.publishedPeriod ?: "2026") }
    var description by remember { mutableStateOf(initialBook?.description ?: "") }
    var sampleContent by remember { mutableStateOf(initialBook?.sampleContent ?: "") }
    var fullContent by remember { mutableStateOf(initialBook?.fullContent ?: "") }
    var pageCountText by remember { mutableStateOf((initialBook?.pageCount ?: 120).toString()) }

    // Cover image states
    var coverImageUrl by remember { mutableStateOf(initialBook?.coverImageUrl ?: "") }
    var coverStyleIndex by remember { mutableIntStateOf(initialBook?.coverStyleIndex ?: 0) }
    var coverInputMode by remember { mutableStateOf("PRESET") } // "PRESET", "GALLERY", "URL"

    // Pricing states
    var isFree by remember { mutableStateOf(initialBook?.isFree ?: false) }
    var priceDzdText by remember { mutableStateOf((initialBook?.priceDzd?.toInt() ?: 1200).toString()) }
    var priceUsdtText by remember { mutableStateOf((initialBook?.priceUsdt ?: 6.0).toString()) }

    // Validation
    var titleError by remember { mutableStateOf<String?>(null) }
    var authorError by remember { mutableStateOf<String?>(null) }

    // Photo Picker launcher for zero-permission gallery upload
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            coverImageUrl = uri.toString()
            coverInputMode = "GALLERY"
        }
    }

    // Auto-fill when Gemini AI generates a draft
    LaunchedEffect(aiDraftState) {
        if (aiDraftState is AiDraftState.Success) {
            val draft = aiDraftState.draft
            if (draft.description.isNotBlank()) description = draft.description
            if (draft.sampleContent.isNotBlank()) sampleContent = draft.sampleContent
            if (draft.category.isNotBlank()) category = draft.category
            pageCountText = draft.pageCount.toString()
            priceDzdText = draft.priceDzd.toInt().toString()
            priceUsdtText = draft.priceUsdt.toString()
            onResetAiDraftState()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("author_book_upload_form")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Form Header & Firestore Sync Indicator ---
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
                    shape = CircleShape,
                    color = GoldPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "رفع الكتاب إلى Firestore",
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = if (initialBook == null) "نموذج نشر كتاب جديد" else "تعديل بيانات الكتاب",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "The Illuminated Open Library • مزامنة سحابية مع Firestore",
                        fontSize = 12.sp,
                        color = GoldPrimary
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("author_form_close_btn")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "إغلاق النموذج"
                )
            }
        }

        // Firestore Account Status Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (authUser != null) BaridiMobGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (authUser != null) BaridiMobGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (authUser != null) Icons.Default.CloudDone else Icons.Outlined.Info,
                        contentDescription = null,
                        tint = if (authUser != null) BaridiMobGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (authUser != null) {
                            "متصل سحابياً: سيتم النشر إلى Firestore بحساب ${authUser.displayName ?: "المؤلف"}"
                        } else {
                            "المزامنة مع Firestore: يفضل تسجيل الدخول بحساب Google لتوثيق ملكية الكتاب سحابياً"
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (authUser == null) {
                    OutlinedButton(
                        onClick = onOpenAuth,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("author_form_login_btn")
                    ) {
                        Text("تسجيل الدخول", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- Live Book Preview Card ---
        Text(
            text = "معاينة الغلاف والبطاقة:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LiveBookPreviewCard(
            title = title.ifBlank { "عنوان الكتاب المنير" },
            author = author.ifBlank { "اسم المؤلف" },
            category = category,
            publishedPeriod = publishedPeriod,
            coverImageUrl = coverImageUrl,
            coverStyleIndex = coverStyleIndex,
            priceDzd = priceDzdText.toDoubleOrNull() ?: 1200.0,
            priceUsdt = priceUsdtText.toDoubleOrNull() ?: 6.0,
            isFree = isFree
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        // --- 1. Basic Details (Title & Author) ---
        Text(
            text = "١. المعلومات الأساسية والتصنيف",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = GoldPrimary
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                if (it.isNotBlank()) titleError = null
            },
            label = { Text("عنوان الكتاب *") },
            placeholder = { Text("مثال: ومضات في الفكر والحرية") },
            leadingIcon = {
                Icon(Icons.Default.Title, contentDescription = null, tint = GoldPrimary)
            },
            isError = titleError != null,
            supportingText = titleError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("author_form_title_input")
        )

        // Gemini AI Draft Assistant Button
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = GoldPrimary.copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f)),
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
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "مساعد التأليف الذكي (Gemini)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "توليد وصف ومحتوى وتسعير مقترح تلقائياً",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { onGenerateWithAi(title.ifBlank { "كتاب في المعرفة والإلهام" }) },
                    enabled = aiDraftState !is AiDraftState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color(0xFF1A1500)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("author_form_gemini_btn")
                ) {
                    if (aiDraftState is AiDraftState.Loading) {
                        CircularProgressIndicator(
                            color = Color(0xFF1A1500),
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("جاري...", fontSize = 11.sp)
                    } else {
                        Text("✨ توليد بـ Gemini", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (aiDraftState is AiDraftState.Error) {
            Text(
                text = aiDraftState.message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = author,
                onValueChange = {
                    author = it
                    if (it.isNotBlank()) authorError = null
                },
                label = { Text("اسم المؤلف *") },
                placeholder = { Text("أنا (المؤلف)") },
                isError = authorError != null,
                supportingText = authorError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .testTag("author_form_author_input")
            )

            OutlinedTextField(
                value = publishedPeriod,
                onValueChange = { publishedPeriod = it },
                label = { Text("فترة النشر") },
                placeholder = { Text("2026") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(0.8f)
                    .testTag("author_form_period_input")
            )
        }

        // Category Selection
        Text(
            text = "تصنيف الكتاب:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BookCategories.take(4).forEach { cat ->
                FilterChip(
                    selected = category == cat,
                    onClick = { category = cat },
                    label = { Text(cat, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = Color(0xFF1A1500)
                    )
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BookCategories.drop(4).forEach { cat ->
                FilterChip(
                    selected = category == cat,
                    onClick = { category = cat },
                    label = { Text(cat, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = Color(0xFF1A1500)
                    )
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        // --- 2. Cover Image Section ---
        Text(
            text = "٢. صورة الغلاف والتصميم الفني",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = GoldPrimary
        )

        // Tabs to choose Cover Method
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = coverInputMode == "PRESET",
                onClick = { coverInputMode = "PRESET" },
                label = { Text("قوالب منيرة جاهزة", fontSize = 11.sp) },
                leadingIcon = {
                    Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = coverInputMode == "GALLERY",
                onClick = { coverInputMode = "GALLERY" },
                label = { Text("رفع من جهازي", fontSize = 11.sp) },
                leadingIcon = {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = coverInputMode == "URL",
                onClick = { coverInputMode = "URL" },
                label = { Text("رابط صورة", fontSize = 11.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                modifier = Modifier.weight(1f)
            )
        }

        when (coverInputMode) {
            "PRESET" -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "اختر تدرجاً فنياً إسلامياً منيراً لغلاف الكتاب:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CoverGradients.forEachIndexed { index, gradient ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Brush.linearGradient(gradient))
                                    .border(
                                        width = if (coverStyleIndex == index && coverImageUrl.isBlank()) 3.dp else 1.dp,
                                        color = if (coverStyleIndex == index && coverImageUrl.isBlank()) GoldPrimary else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        coverStyleIndex = index
                                        coverImageUrl = ""
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (coverStyleIndex == index && coverImageUrl.isBlank()) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "محدد",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            "GALLERY" -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("author_form_pick_cover_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (coverImageUrl.isNotBlank()) "تغيير صورة الغلاف المحددة" else "اختيار صورة غلاف من المعرض",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (coverImageUrl.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تم اختيار صورة غلاف محلية بنجاح ✓",
                                fontSize = 12.sp,
                                color = BaridiMobGreen,
                                fontWeight = FontWeight.Medium
                            )
                            OutlinedButton(
                                onClick = { coverImageUrl = "" },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("إلغاء الصورة", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
            "URL" -> {
                OutlinedTextField(
                    value = coverImageUrl,
                    onValueChange = { coverImageUrl = it },
                    label = { Text("رابط صورة الغلاف المباشر (URL)") },
                    placeholder = { Text("https://images.unsplash.com/photo-...") },
                    leadingIcon = {
                        Icon(Icons.Default.Image, contentDescription = null, tint = GoldPrimary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("author_form_cover_url_input")
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        // --- 3. Description & Excerpts ---
        Text(
            text = "٣. وصف وملخص ومحتوى الكتاب",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = GoldPrimary
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("نبذة ووصف الكتاب") },
            placeholder = { Text("اكتب نبذة شيقة تجذب القراء إلى فكرة الكتاب وموضوعه...") },
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("author_form_desc_input")
        )

        OutlinedTextField(
            value = sampleContent,
            onValueChange = { sampleContent = it },
            label = { Text("فصل تمهيدي أو مقتطف تجريبي (متاح مجاناً للجميع)") },
            placeholder = { Text("ضع نموذجاً أو فصلاً تجريبياً يمكن للقارئ تصفحه قبل الشراء...") },
            minLines = 3,
            maxLines = 6,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("author_form_sample_input")
        )

        OutlinedTextField(
            value = fullContent,
            onValueChange = { fullContent = it },
            label = { Text("النص الكامل للكتاب (للقراء بعد الشراء أو إذا كان مجانياً)") },
            placeholder = { Text("ضع متن الكتاب وفصوله الكاملة هنا...") },
            minLines = 4,
            maxLines = 8,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("author_form_full_content_input")
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        // --- 4. Pricing & Payment Options (BaridiMob & Binance) ---
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = GoldPrimary
                        )
                        Column {
                            Text(
                                text = "تسعير الكتاب وطرق الدفع",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "دعم كامل لبريدي موب (الجزائر) وبينانس (الخارج)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isFree) "كتاب مجاني" else "كتاب مدفوع",
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
                            modifier = Modifier.testTag("author_form_is_free_switch")
                        )
                    }
                }

                AnimatedVisibility(visible = !isFree) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Algeria BaridiMob Price
                            OutlinedTextField(
                                value = priceDzdText,
                                onValueChange = { priceDzdText = it.filter { ch -> ch.isDigit() } },
                                label = { Text("الجزائر (د.ج)") },
                                placeholder = { Text("1200") },
                                leadingIcon = {
                                    Text("🇩🇿", fontSize = 16.sp)
                                },
                                trailingIcon = {
                                    Text("د.ج", fontSize = 12.sp, color = BaridiMobGreen, fontWeight = FontWeight.Bold)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("author_form_price_dzd_input")
                            )

                            // International Binance USDT Price
                            OutlinedTextField(
                                value = priceUsdtText,
                                onValueChange = { priceUsdtText = it },
                                label = { Text("الخارج (Binance)") },
                                placeholder = { Text("6.0") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CurrencyBitcoin,
                                        contentDescription = null,
                                        tint = BinanceYellow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    Text("USDT", fontSize = 12.sp, color = BinanceYellow, fontWeight = FontWeight.Bold)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("author_form_price_usdt_input")
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 سيتمكن القراء من الجزائر من الدفع فوراً عبر تطبيق BaridiMob، بينما يمكن للقراء من الخارج التحويل عبر Binance Pay (USDT).",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        // --- 5. Save Button with Firestore Persistence ---
        Button(
            onClick = {
                if (title.isBlank()) {
                    titleError = "يرجى إدخال عنوان الكتاب"
                    return@Button
                }
                if (author.isBlank()) {
                    authorError = "يرجى إدخال اسم المؤلف"
                    return@Button
                }

                val dzd = priceDzdText.toDoubleOrNull() ?: 1200.0
                val usdt = priceUsdtText.toDoubleOrNull() ?: 6.0
                val pages = pageCountText.toIntOrNull() ?: 120

                val finalBook = (initialBook ?: Book(title = title)).copy(
                    title = title.trim(),
                    author = author.trim(),
                    category = category,
                    publishedPeriod = publishedPeriod.trim().ifBlank { "2026" },
                    description = description.trim(),
                    sampleContent = sampleContent.trim(),
                    fullContent = fullContent.trim(),
                    pageCount = pages,
                    isFree = isFree,
                    priceDzd = if (isFree) 0.0 else dzd,
                    priceUsdt = if (isFree) 0.0 else usdt,
                    coverImageUrl = coverImageUrl.trim(),
                    coverStyleIndex = coverStyleIndex
                )
                onSaveBookToFirestore(finalBook)
            },
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("author_form_save_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldPrimary,
                contentColor = Color(0xFF1A1500)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color = Color(0xFF1A1500),
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "جاري الحفظ والمزامنة مع Firestore...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (initialBook == null) "حفظ ونشر الكتاب إلى Firestore ☁️" else "تحديث وحفظ التعديلات في Firestore ☁️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

/**
 * Fullscreen or Dialog modal wrapping the [AuthorBookUploadForm].
 */
@Composable
fun AuthorBookUploadDialog(
    initialBook: Book? = null,
    authUser: AuthUser? = null,
    isSaving: Boolean = false,
    aiDraftState: AiDraftState = AiDraftState.Idle,
    onGenerateWithAi: (String) -> Unit = {},
    onResetAiDraftState: () -> Unit = {},
    onOpenAuth: () -> Unit = {},
    onDismiss: () -> Unit,
    onSaveBookToFirestore: (Book) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .heightIn(max = 760.dp)
                .testTag("author_book_upload_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                AuthorBookUploadForm(
                    initialBook = initialBook,
                    authUser = authUser,
                    isSaving = isSaving,
                    aiDraftState = aiDraftState,
                    onGenerateWithAi = onGenerateWithAi,
                    onResetAiDraftState = onResetAiDraftState,
                    onOpenAuth = onOpenAuth,
                    onDismiss = onDismiss,
                    onSaveBookToFirestore = onSaveBookToFirestore
                )
            }
        }
    }
}

/**
 * Live interactive preview card displaying how the book cover and info look.
 */
@Composable
fun LiveBookPreviewCard(
    title: String,
    author: String,
    category: String,
    publishedPeriod: String,
    coverImageUrl: String,
    coverStyleIndex: Int,
    priceDzd: Double,
    priceUsdt: Double,
    isFree: Boolean,
    modifier: Modifier = Modifier
) {
    val gradientColors = CoverGradients.getOrElse(coverStyleIndex % CoverGradients.size) {
        CoverGradients.first()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_book_preview_card")
            .border(
                1.dp,
                GoldPrimary.copy(alpha = 0.5f),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Cover Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Brush.linearGradient(gradientColors))
            ) {
                if (coverImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = coverImageUrl,
                        contentDescription = "غلاف $title",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                    // Semi-transparent overlay to ensure readability
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.45f))
                    )
                }

                // Header Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = category,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "فترة: $publishedPeriod",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Title and Author on Cover
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "بقلم: $author",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
            }

            // Pricing details bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isFree) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BaridiMobGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "كتاب مجاني ومفتوح 📖",
                            color = BaridiMobGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BaridiMobGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "🇩🇿 ${priceDzd.toInt()} د.ج",
                                color = BaridiMobGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BinanceYellow.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "🌐 $priceUsdt USDT",
                                color = BinanceYellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "The Illuminated Library",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

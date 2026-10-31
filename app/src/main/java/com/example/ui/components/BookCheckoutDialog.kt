package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.OpenInNew
import com.example.data.model.Book
import com.example.data.model.PaymentSettings
import com.example.data.payment.BaridiMobPaymentProcessor
import com.example.data.payment.PaymentMethod
import com.example.ui.theme.BaridiMobGreen
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.GoldPrimary

@Composable
fun BookCheckoutDialog(
    book: Book,
    paymentSettings: PaymentSettings,
    onDismiss: () -> Unit,
    onConfirmPayment: (method: String, amount: String, ref: String, contact: String, note: String) -> Unit
) {
    val context = LocalContext.current
    var selectedMethodTab by remember { mutableIntStateOf(0) } // 0: BaridiMob (Algeria), 1: Binance (International)

    var transactionReference by remember { mutableStateOf("") }
    var customerContact by remember { mutableStateOf("") }
    var customerNote by remember { mutableStateOf("") }
    var refError by remember { mutableStateOf(false) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "تم نسخ $label بنجاح!", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp)
                .testTag("checkout_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
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
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = GoldPrimary
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "شراء واقتناء الكتاب",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = book.title,
                                fontSize = 13.sp,
                                color = GoldPrimary,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Payment Method Tabs (BaridiMob vs Binance)
                TabRow(
                    selectedTabIndex = selectedMethodTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedMethodTab]),
                            color = if (selectedMethodTab == 0) BaridiMobGreen else BinanceYellow
                        )
                    }
                ) {
                    Tab(
                        selected = selectedMethodTab == 0,
                        onClick = { selectedMethodTab = 0 },
                        text = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "🇩🇿 بريدي موب (الجزائر)",
                                    fontWeight = if (selectedMethodTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedMethodTab == 0) BaridiMobGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "${book.priceDzd.toInt()} د.ج",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BaridiMobGreen
                                )
                            }
                        }
                    )
                    Tab(
                        selected = selectedMethodTab == 1,
                        onClick = { selectedMethodTab = 1 },
                        text = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "🌐 منصة Binance (دولي)",
                                    fontWeight = if (selectedMethodTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedMethodTab == 1) BinanceYellow else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "${book.priceUsdt} USDT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BinanceYellow
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable details and form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (selectedMethodTab == 0) {
                        // Algeria BaridiMob Payment Card
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = BaridiMobGreen.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BaridiMobGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "المبلغ المطلوب:",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${book.priceDzd.toInt()} دينار جزائري (DZD)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = BaridiMobGreen
                                    )
                                }

                                HorizontalDivider(color = BaridiMobGreen.copy(alpha = 0.2f))

                                // RIP Field with Copy Button
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "رقم الحساب البريدي (RIP):",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                MaterialTheme.colorScheme.surface,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = paymentSettings.baridiMobRip,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        IconButton(
                                            onClick = { copyToClipboard("رقم RIP", paymentSettings.baridiMobRip) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "نسخ RIP",
                                                tint = BaridiMobGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                // Account Holder
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("اسم صاحب الحساب:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(paymentSettings.baridiMobAccountHolder, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                if (paymentSettings.baridiMobPhone.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("الهاتف المربوط ببريدي موب:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(paymentSettings.baridiMobPhone, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Instructions step-by-step
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("خطوات التحويل عبر تطبيق BaridiMob:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("1. اضغط زر 'فتح تطبيق بريدي موب' أدناه (سيتم نسخ الـ RIP تلقائياً).", fontSize = 11.sp)
                                        Text("2. اختر 'تحويل' (Virement) وألصق رقم RIP وأدخل ${book.priceDzd.toInt()} د.ج.", fontSize = 11.sp)
                                        Text("3. أكد التحويل واكتب رقم وصل العملية في الحقل أدناه.", fontSize = 11.sp)
                                    }
                                }

                                val baridiMobPayment = remember(book, paymentSettings) {
                                    PaymentMethod.BaridiMob(
                                        ripNumber = paymentSettings.baridiMobRip,
                                        accountHolder = paymentSettings.baridiMobAccountHolder,
                                        amountDzd = book.priceDzd,
                                        orderReference = "ORD-${book.id}-${System.currentTimeMillis().toString().takeLast(5)}",
                                        phoneNumber = paymentSettings.baridiMobPhone,
                                        ccpNumber = paymentSettings.baridiMobCcpNumber
                                    )
                                }
                                val isBaridiMobInstalled = remember(context) {
                                    BaridiMobPaymentProcessor.isBaridiMobInstalled(context)
                                }

                                // Status Badge & Launch External BaridiMob Button
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isBaridiMobInstalled) BaridiMobGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isBaridiMobInstalled) Icons.Default.CheckCircle else Icons.Default.Download,
                                        contentDescription = null,
                                        tint = if (isBaridiMobInstalled) BaridiMobGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (isBaridiMobInstalled) "تطبيق بريدي موب متوفر ومثبت على هاتفك" else "تطبيق بريدي موب غير مثبت (يمكنك تحميله من متجر Play)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isBaridiMobInstalled) BaridiMobGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Button(
                                    onClick = {
                                        val launchResult = BaridiMobPaymentProcessor.launchTransaction(
                                            context = context,
                                            payment = baridiMobPayment,
                                            autoCopyRip = true
                                        )
                                        Toast.makeText(context, launchResult.message, Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .testTag("launch_baridimob_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BaridiMobGreen,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isBaridiMobInstalled) Icons.Default.OpenInNew else Icons.Default.Download,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isBaridiMobInstalled) "فتح تطبيق بريدي موب للتحويل الآن" else "تحميل بريدي موب ونسخ رقم RIP",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        // International Binance Payment Card
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = BinanceYellow.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BinanceYellow.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "المبلغ المطلوب:",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${book.priceUsdt} USDT",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = BinanceYellow
                                    )
                                }

                                HorizontalDivider(color = BinanceYellow.copy(alpha = 0.3f))

                                // Binance Pay ID with Copy Button
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Binance Pay ID (معرف الدفع المباشر):",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                MaterialTheme.colorScheme.surface,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = paymentSettings.binancePayId,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        IconButton(
                                            onClick = { copyToClipboard("Binance Pay ID", paymentSettings.binancePayId) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "نسخ Pay ID",
                                                tint = BinanceYellow,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                // USDT Wallet Address
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "عنوان المحفظة (USDT Wallet):",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "الشبكة: ${paymentSettings.binanceNetwork}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BinanceYellow
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                MaterialTheme.colorScheme.surface,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = paymentSettings.binanceUsdtAddress,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = { copyToClipboard("عنوان المحفظة", paymentSettings.binanceUsdtAddress) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "نسخ العنوان",
                                                tint = BinanceYellow,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                // Step-by-step
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("طريقة الإرسال عبر تطبيق Binance:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("1. افتح تطبيق Binance واختر Pay > Send.", fontSize = 11.sp)
                                        Text("2. أدخل Pay ID أو عنوان المحفظة وأرسل ${book.priceUsdt} USDT.", fontSize = 11.sp)
                                        Text("3. ضع رقم المعاملة (TxID / Order ID) في الخانة أدناه.", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // User proof & submission fields
                    Text(
                        text = "بيانات تأكيد الدفع واستلام الكتاب:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = transactionReference,
                        onValueChange = {
                            transactionReference = it
                            if (it.isNotBlank()) refError = false
                        },
                        label = {
                            Text(if (selectedMethodTab == 0) "رقم وصل التحويل (BaridiMob / CCP TxID) *" else "رقم المعاملة (TxID / Order Ref) *")
                        },
                        placeholder = {
                            Text(if (selectedMethodTab == 0) "مثال: 9847291038" else "مثال: 0x8a9f4... أو 84920193")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transaction_reference_input"),
                        isError = refError,
                        supportingText = if (refError) {
                            { Text("يرجى إدخال رقم الوصل أو المعاملة للتأكيد", color = MaterialTheme.colorScheme.error) }
                        } else null,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = customerContact,
                        onValueChange = { customerContact = it },
                        label = { Text("رقم هاتفك أو بريدك الإلكتروني (اختياري)") },
                        placeholder = { Text("0661123456 أو user@domain.com") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = customerNote,
                        onValueChange = { customerNote = it },
                        label = { Text("ملاحظة أو استفسار للمؤلف (اختياري)") },
                        placeholder = { Text("اكتب أي ملاحظة تود إرسالها...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                // Confirm and Unlock Button
                Button(
                    onClick = {
                        if (transactionReference.isBlank()) {
                            refError = true
                            return@Button
                        }
                        val method = if (selectedMethodTab == 0) "BARIDIMOB" else "BINANCE"
                        val amount = if (selectedMethodTab == 0) "${book.priceDzd.toInt()} DZD" else "${book.priceUsdt} USDT"
                        onConfirmPayment(
                            method,
                            amount,
                            transactionReference.trim(),
                            customerContact.trim(),
                            customerNote.trim()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_checkout_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMethodTab == 0) BaridiMobGreen else BinanceYellow,
                        contentColor = if (selectedMethodTab == 0) Color.White else Color(0xFF1E2026)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تأكيد التحويل وفتح الكتاب للقراءة فوراً",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

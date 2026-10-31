package com.example.ui.components

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
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PaymentSettings
import com.example.ui.theme.BaridiMobGreen
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.GoldPrimary

@Composable
fun PaymentSettingsDialog(
    currentSettings: PaymentSettings,
    onDismiss: () -> Unit,
    onSave: (PaymentSettings) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: BaridiMob (الجزائر), 1: Binance (دولي), 2: تعليمات عامة

    // BaridiMob state
    var rip by remember { mutableStateOf(currentSettings.baridiMobRip) }
    var accountHolder by remember { mutableStateOf(currentSettings.baridiMobAccountHolder) }
    var phone by remember { mutableStateOf(currentSettings.baridiMobPhone) }
    var ccp by remember { mutableStateOf(currentSettings.baridiMobCcpNumber) }

    // Binance state
    var binancePayId by remember { mutableStateOf(currentSettings.binancePayId) }
    var binanceAddress by remember { mutableStateOf(currentSettings.binanceUsdtAddress) }
    var binanceNetwork by remember { mutableStateOf(currentSettings.binanceNetwork) }
    var binanceEmail by remember { mutableStateOf(currentSettings.binanceEmail) }

    // Instructions
    var instructionsArabic by remember { mutableStateOf(currentSettings.instructionsArabic) }
    var instructionsEnglish by remember { mutableStateOf(currentSettings.instructionsEnglish) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 700.dp)
                .testTag("payment_settings_dialog"),
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
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = GoldPrimary
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "تحديد وضبط طرق الدفع",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "بريدي موب (الجزائر) 🇩🇿 | بينانس (دولي) 🌐",
                                fontSize = 12.sp,
                                color = GoldPrimary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs: Algeria (BaridiMob) vs International (Binance)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = if (selectedTab == 0) BaridiMobGreen else if (selectedTab == 1) BinanceYellow else GoldPrimary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🇩🇿 بريدي موب", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🌐 Binance بينانس", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text("التعليمات", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            // BaridiMob Settings Tab
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BaridiMobGreen.copy(alpha = 0.1f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BaridiMobGreen.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Savings,
                                        contentDescription = null,
                                        tint = BaridiMobGreen
                                    )
                                    Column {
                                        Text(
                                            text = "إعدادات تطبيق بريدي موب (BaridiMob)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = BaridiMobGreen
                                        )
                                        Text(
                                            text = "مخصصة للمتعاملين والمشترين من داخل الجزائر للدفع بالدينار الجزائري (DZD)",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = rip,
                                onValueChange = { rip = it },
                                label = { Text("رقم الحساب البريدي الجاري (RIP - 20 رقماً) *") },
                                placeholder = { Text("00799999000123456789") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("baridimob_rip_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = accountHolder,
                                onValueChange = { accountHolder = it },
                                label = { Text("اسم ولقب صاحب الحساب البريدي (Account Holder) *") },
                                placeholder = { Text("فلان الفلاني") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("baridimob_name_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("رقم الهاتف المربوط ببريدي موب (لتأكيد الإشعار)") },
                                placeholder = { Text("0550123456") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = ccp,
                                onValueChange = { ccp = it },
                                label = { Text("رقم الحساب الجاري التقليدي (CCP + Clé)") },
                                placeholder = { Text("12345678 Clé 90") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        1 -> {
                            // Binance Settings Tab
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BinanceYellow.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BinanceYellow.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CurrencyBitcoin,
                                        contentDescription = null,
                                        tint = BinanceYellow
                                    )
                                    Column {
                                        Text(
                                            text = "إعدادات منصة بينانس (Binance Pay / USDT)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = BinanceYellow
                                        )
                                        Text(
                                            text = "مخصصة للمتعاملين من خارج الجزائر للدفع بالعملات الرقمية العالمية (USDT)",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = binancePayId,
                                onValueChange = { binancePayId = it },
                                label = { Text("Binance Pay ID (معرف بينانس باي) *") },
                                placeholder = { Text("مثال: 584920193") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("binance_payid_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = binanceAddress,
                                onValueChange = { binanceAddress = it },
                                label = { Text("عنوان المحفظة USDT Wallet Address *") },
                                placeholder = { Text("TYs9QxM8XvXk9Vn3PqWbZ2Lm7Jk4RtE1Au") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("binance_address_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = binanceNetwork,
                                onValueChange = { binanceNetwork = it },
                                label = { Text("الشبكة المفضلة (Transfer Network)") },
                                placeholder = { Text("USDT (TRC20) أو USDT (BEP20)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = binanceEmail,
                                onValueChange = { binanceEmail = it },
                                label = { Text("البريد الإلكتروني المربوط بحساب بينانس") },
                                placeholder = { Text("name@domain.com") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        2 -> {
                            // General Instructions Tab
                            OutlinedTextField(
                                value = instructionsArabic,
                                onValueChange = { instructionsArabic = it },
                                label = { Text("تعليمات الدفع (باللغة العربية)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = instructionsEnglish,
                                onValueChange = { instructionsEnglish = it },
                                label = { Text("Payment Instructions (English)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                // Save button
                Button(
                    onClick = {
                        val updated = currentSettings.copy(
                            baridiMobRip = rip.trim(),
                            baridiMobAccountHolder = accountHolder.trim(),
                            baridiMobPhone = phone.trim(),
                            baridiMobCcpNumber = ccp.trim(),
                            binancePayId = binancePayId.trim(),
                            binanceUsdtAddress = binanceAddress.trim(),
                            binanceNetwork = binanceNetwork.trim(),
                            binanceEmail = binanceEmail.trim(),
                            instructionsArabic = instructionsArabic.trim(),
                            instructionsEnglish = instructionsEnglish.trim()
                        )
                        onSave(updated)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_payment_settings_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color(0xFF1A1500)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "حفظ وتفعيل طرق الدفع (بريدي موب + بينانس)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

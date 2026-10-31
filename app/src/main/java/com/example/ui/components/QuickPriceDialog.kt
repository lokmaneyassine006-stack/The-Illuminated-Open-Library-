package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Book
import com.example.ui.theme.BaridiMobGreen
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.GoldPrimary

@Composable
fun QuickPriceDialog(
    book: Book,
    onDismiss: () -> Unit,
    onSavePrice: (bookId: Long, priceDzd: Double, priceUsdt: Double, isFree: Boolean) -> Unit
) {
    var isFree by remember { mutableStateOf(book.isFree) }
    var priceDzdText by remember { mutableStateOf(book.priceDzd.toInt().toString()) }
    var priceUsdtText by remember { mutableStateOf(book.priceUsdt.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("quick_price_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PriceChange,
                                    contentDescription = null,
                                    tint = GoldPrimary
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "تحديد سعر الكتاب",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = book.title,
                                fontSize = 12.sp,
                                color = GoldPrimary,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Free toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isFree) "كتاب مجاني ومفتوح" else "كتاب مدفوع",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isFree) "متاح للقراءة مجاناً لجميع الزوار" else "يتطلب الدفع عبر بريدي موب أو بينانس",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = !isFree,
                        onCheckedChange = { isFree = !it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GoldPrimary,
                            checkedTrackColor = GoldPrimary.copy(alpha = 0.4f)
                        )
                    )
                }

                if (!isFree) {
                    // Quick Price Presets
                    Text(
                        text = "خيارات تسعير سريعة:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val presets = listOf(
                            Triple("500 د.ج", "500", "2.5"),
                            Triple("1,000 د.ج", "1000", "5.0"),
                            Triple("2,000 د.ج", "2000", "10.0"),
                            Triple("3,500 د.ج", "3500", "17.0")
                        )
                        presets.forEach { (label, dzdVal, usdtVal) ->
                            FilterChip(
                                selected = priceDzdText == dzdVal,
                                onClick = {
                                    priceDzdText = dzdVal
                                    priceUsdtText = usdtVal
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = Color(0xFF1A1500)
                                )
                            )
                        }
                    }

                    // Price DZD for Algeria BaridiMob
                    OutlinedTextField(
                        value = priceDzdText,
                        onValueChange = { priceDzdText = it.filter { c -> c.isDigit() } },
                        label = { Text("السعر في الجزائر (بريدي موب بالدينار د.ج)") },
                        trailingIcon = { Text("DZD", fontWeight = FontWeight.Bold, color = BaridiMobGreen, modifier = Modifier.padding(end = 8.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("modal_price_dzd_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Price USDT for International Binance
                    OutlinedTextField(
                        value = priceUsdtText,
                        onValueChange = { priceUsdtText = it },
                        label = { Text("السعر للمتعاملين من الخارج (Binance USDT)") },
                        trailingIcon = { Text("USDT", fontWeight = FontWeight.Bold, color = BinanceYellow, modifier = Modifier.padding(end = 8.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("modal_price_usdt_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Confirm Save
                Button(
                    onClick = {
                        val dzd = priceDzdText.toDoubleOrNull() ?: 0.0
                        val usdt = priceUsdtText.toDoubleOrNull() ?: 0.0
                        onSavePrice(book.id, if (isFree) 0.0 else dzd, if (isFree) 0.0 else usdt, isFree)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_price_save_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color(0xFF1A1500)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "حفظ وتحديث سعر الكتاب",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

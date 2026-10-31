package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Book
import com.example.ui.theme.BaridiMobGreen
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary

@Composable
fun BookReaderDialog(
    book: Book,
    onDismiss: () -> Unit,
    onBuyFullVersion: () -> Unit,
    onAskAi: () -> Unit = {}
) {
    var fontSize by remember { mutableFloatStateOf(16f) }
    val isSampleOnly = !book.isUnlocked && !book.isFree
    val contentToDisplay = if (isSampleOnly) {
        if (book.sampleContent.isNotBlank()) book.sampleContent else book.description
    } else {
        if (book.fullContent.isNotBlank()) book.fullContent else book.sampleContent.ifBlank { book.description }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("book_reader_dialog"),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "رجوع", tint = MaterialTheme.colorScheme.onBackground)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = book.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1
                        )
                        Text(
                            text = if (isSampleOnly) "عينة مجانية للقراءة" else "النسخة الكاملة المعتمدة",
                            fontSize = 11.sp,
                            color = if (isSampleOnly) GoldPrimary else BaridiMobGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // AI Assistant & Font Size Controllers
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldPrimary.copy(alpha = 0.2f),
                            modifier = Modifier.clickable { onAskAi() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.AutoAwesome,
                                    contentDescription = "اسأل Gemini",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("Gemini", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            }
                        }

                        IconButton(
                            onClick = { if (fontSize > 13f) fontSize -= 2f },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("A-", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        IconButton(
                            onClick = { if (fontSize < 26f) fontSize += 2f },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("A+", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Reader Parchment Scroll View
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Book Header Banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = book.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = GoldPrimary
                                    )
                                    Text(
                                        text = "المؤلف: ${book.author} • التصنيف: ${book.category}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldPrimary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${book.pageCount} صفحة",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Text content
                        Text(
                            text = contentToDisplay.ifBlank { "لم تتم إضافة نصوص بعد لهذا الكتاب." },
                            fontSize = fontSize.sp,
                            lineHeight = (fontSize * 1.6f).sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isSampleOnly) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = GoldPrimary.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "نهاية العينة المجانية",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = GoldPrimary
                                    )
                                    Text(
                                        text = "للحصول على كامل محتوى الكتاب، يمكنك الشراء عبر بريدي موب (الجزائر) أو بينانس (دولي)",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Button(
                                        onClick = {
                                            onDismiss()
                                            onBuyFullVersion()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = BaridiMobGreen,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("اقتناء النسخة الكاملة الآن")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

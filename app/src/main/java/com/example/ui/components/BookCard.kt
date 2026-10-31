package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.data.model.Book
import com.example.ui.theme.BaridiMobGreen
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.CoverGradients
import com.example.ui.theme.GoldPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookCard(
    book: Book,
    onReadClick: () -> Unit,
    onBuyClick: () -> Unit,
    onEditPriceClick: () -> Unit,
    onEditBookClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAiAssistantClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val gradientColors = CoverGradients.getOrElse(book.coverStyleIndex % CoverGradients.size) {
        CoverGradients.first()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("book_card_${book.id}")
            .border(
                width = 1.dp,
                color = if (book.isUnlocked || book.isFree) GoldPrimary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Illuminated Book Spine / Cover Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.linearGradient(
                            colors = gradientColors
                        )
                    )
            ) {
                if (book.coverImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = book.coverImageUrl,
                        contentDescription = "غلاف ${book.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.45f))
                    )
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(16.dp)
                ) {
                    // Category & Period Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = book.category,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = "فترة: ${book.publishedPeriod}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Book Title & Author overlay on cover
                Column(
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(
                        text = book.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "بقلم: ${book.author}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }

                // Unlocked / Purchased Badge
                if (book.isUnlocked || book.isFree) {
                    Surface(
                        shape = CircleShape,
                        color = BaridiMobGreen,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "متاح للقراءة",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

            // Body Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (book.description.isNotBlank()) {
                    Text(
                        text = book.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Pricing Bar (BaridiMob DZD vs Binance USDT)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        if (book.isFree || (book.priceDzd <= 0.0 && book.priceUsdt <= 0.0)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "✨ كتاب مجاني ومفتوح للجميع",
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Algeria BaridiMob Price
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(BaridiMobGreen)
                                    )
                                    Text(
                                        text = "الجزائر (بريدي موب):",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${book.priceDzd.toInt()} د.ج",
                                        fontWeight = FontWeight.Bold,
                                        color = BaridiMobGreen,
                                        fontSize = 13.sp
                                    )
                                }

                                // International Binance Price
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(BinanceYellow)
                                    )
                                    Text(
                                        text = "دولي (Binance):",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${book.priceUsdt} USDT",
                                        fontWeight = FontWeight.Bold,
                                        color = BinanceYellow,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (book.isUnlocked || book.isFree) {
                        Button(
                            onClick = onReadClick,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("read_book_btn_${book.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = Color(0xFF1A1500)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("قراءة الكتاب", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onBuyClick,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("buy_book_btn_${book.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BaridiMobGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("شراء / دفع", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onReadClick,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("عينة", fontSize = 12.sp)
                        }
                    }

                    // Direct Gemini AI Assistant button
                    IconButton(
                        onClick = onAiAssistantClick,
                        modifier = Modifier
                            .testTag("gemini_assistant_btn_${book.id}")
                            .background(
                                GoldPrimary.copy(alpha = 0.15f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.AutoAwesome,
                            contentDescription = "مساعد Gemini",
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Direct Edit Price button (Requested explicitly by user)
                    IconButton(
                        onClick = onEditPriceClick,
                        modifier = Modifier
                            .testTag("edit_price_btn_${book.id}")
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PriceChange,
                            contentDescription = "تحديد السعر",
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Edit Book
                    IconButton(
                        onClick = onEditBookClick,
                        modifier = Modifier
                            .testTag("edit_book_btn_${book.id}")
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل الكتاب",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete Book
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .testTag("delete_book_btn_${book.id}")
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف الكتاب",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

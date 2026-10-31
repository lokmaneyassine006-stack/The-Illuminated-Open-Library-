package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_settings")
data class PaymentSettings(
    @PrimaryKey
    val id: Int = 1,
    // بيانات بريدي موب (الجزائر)
    val baridiMobRip: String = "00799999000123456789",
    val baridiMobAccountHolder: String = "المكتبة المنيرة - The Illuminated Library",
    val baridiMobPhone: String = "0550123456",
    val baridiMobCcpNumber: String = "1234567 Clé 89",
    
    // بيانات بينانس (الخارج / دولي)
    val binancePayId: String = "584920193",
    val binanceUsdtAddress: String = "TYs9QxM8XvXk9Vn3PqWbZ2Lm7Jk4RtE1Au",
    val binanceNetwork: String = "USDT (TRC20)",
    val binanceEmail: String = "payments@illuminated-library.org",
    
    // تعليمات عامة
    val instructionsArabic: String = "يرجى إرسال وصل الدفع أو رقم المعاملة بعد إتمام التحويل لتأكيد فتح الكتاب فورا.",
    val instructionsEnglish: String = "Please send the transaction proof/TxID after transfer to instantly unlock your book."
)

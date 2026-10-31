package com.example.data.payment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing payment methods available in The Illuminated Open Library.
 * Supports localized Algerian operations (BaridiMob) and international cross-border transfers (Binance).
 */
sealed class PaymentMethod {
    abstract val id: String
    abstract val displayNameArabic: String
    abstract val displayNameEnglish: String
    abstract val currencySymbol: String
    abstract val icon: ImageVector

    /**
     * BaridiMob payment method for Algerian users via Algérie Poste.
     *
     * @param ripNumber The 20-digit Relevé d'Identité Postale (CCP + Cle).
     * @param accountHolder The full name on the BaridiMob/Algérie Poste account.
     * @param amountDzd The exact amount in Algerian Dinars (DZD).
     * @param orderReference Unique order reference code (e.g. "ORD-123456").
     * @param phoneNumber Optional associated Algerian mobile number (Mobilis/Djezzy/Ooredoo).
     * @param ccpNumber Optional classical CCP account representation.
     */
    data class BaridiMob(
        val ripNumber: String,
        val accountHolder: String,
        val amountDzd: Double,
        val orderReference: String,
        val phoneNumber: String = "",
        val ccpNumber: String = ""
    ) : PaymentMethod() {
        override val id: String = ID
        override val displayNameArabic: String = "بريدي موب (الجزائر)"
        override val displayNameEnglish: String = "BaridiMob (Algérie Poste)"
        override val currencySymbol: String = "د.ج"
        override val icon: ImageVector = Icons.Default.AccountBalance

        val formattedAmountDzd: String
            get() = "${amountDzd.toInt()} د.ج"

        val formattedAmountEn: String
            get() = "${amountDzd.toInt()} DZD"

        /**
         * Generates a concise summary text suitable for clipboard copy or sharing.
         */
        fun toShareableSummary(): String {
            return buildString {
                append("المستلم: $accountHolder\n")
                append("رقم RIP: $ripNumber\n")
                append("المبلغ: ${amountDzd.toInt()} د.ج\n")
                append("رقم الطلب: $orderReference")
                if (phoneNumber.isNotBlank()) {
                    append("\nالهاتف: $phoneNumber")
                }
            }
        }

        companion object {
            const val ID = "BARIDIMOB"
            const val CURRENCY_CODE = "DZD"
        }
    }

    /**
     * Binance cryptocurrency / Binance Pay method for international users.
     *
     * @param payId The recipient's Binance Pay ID.
     * @param usdtAddress The wallet address for USDT.
     * @param network The blockchain network (e.g. TRC20, BEP20).
     * @param amountUsdt The required amount in USDT.
     * @param orderReference Unique order reference code.
     * @param email Optional Binance registered account email.
     */
    data class Binance(
        val payId: String,
        val usdtAddress: String,
        val network: String = "USDT (TRC20)",
        val amountUsdt: Double,
        val orderReference: String,
        val email: String = ""
    ) : PaymentMethod() {
        override val id: String = ID
        override val displayNameArabic: String = "منصة بينانس (دولي)"
        override val displayNameEnglish: String = "Binance Pay / USDT"
        override val currencySymbol: String = "USDT"
        override val icon: ImageVector = Icons.Default.CurrencyBitcoin

        val formattedAmount: String
            get() = "$amountUsdt USDT"

        companion object {
            const val ID = "BINANCE"
            const val CURRENCY_CODE = "USDT"
        }
    }
}

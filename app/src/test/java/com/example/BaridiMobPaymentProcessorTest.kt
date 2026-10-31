package com.example

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.data.payment.BaridiMobPaymentProcessor
import com.example.data.payment.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BaridiMobPaymentProcessorTest {

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun `test PaymentMethod BaridiMob initialization and formatting`() {
        val payment = PaymentMethod.BaridiMob(
            ripNumber = "00799999000123456789",
            accountHolder = "المكتبة المنيرة",
            amountDzd = 2500.0,
            orderReference = "ORD-98765",
            phoneNumber = "0550123456"
        )

        assertEquals(PaymentMethod.BaridiMob.ID, payment.id)
        assertEquals("بريدي موب (الجزائر)", payment.displayNameArabic)
        assertEquals("د.ج", payment.currencySymbol)
        assertEquals("2500 د.ج", payment.formattedAmountDzd)
        assertEquals("2500 DZD", payment.formattedAmountEn)
        assertTrue(payment.toShareableSummary().contains("00799999000123456789"))
        assertTrue(payment.toShareableSummary().contains("2500 د.ج"))
    }

    @Test
    fun `test PaymentMethod Binance initialization and formatting`() {
        val payment = PaymentMethod.Binance(
            payId = "584920193",
            usdtAddress = "TYs9QxM8XvXk9Vn3PqWbZ2Lm7Jk4RtE1Au",
            network = "USDT (TRC20)",
            amountUsdt = 12.5,
            orderReference = "BIN-12345"
        )

        assertEquals(PaymentMethod.Binance.ID, payment.id)
        assertEquals("منصة بينانس (دولي)", payment.displayNameArabic)
        assertEquals("USDT", payment.currencySymbol)
        assertEquals("12.5 USDT", payment.formattedAmount)
    }

    @Test
    fun `test BaridiMob payment explicit intent creation with extras`() {
        val payment = PaymentMethod.BaridiMob(
            ripNumber = "00799999000123456789",
            accountHolder = "The Illuminated Library",
            amountDzd = 1800.0,
            orderReference = "ORD-123"
        )

        val intent = BaridiMobPaymentProcessor.createExplicitIntent(payment)

        assertEquals(BaridiMobPaymentProcessor.PACKAGE_BARIDIMOB, intent.`package`)
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("00799999000123456789", intent.getStringExtra(BaridiMobPaymentProcessor.EXTRA_RIP))
        assertEquals(1800.0, intent.getDoubleExtra(BaridiMobPaymentProcessor.EXTRA_AMOUNT, 0.0), 0.001)
        assertEquals("ORD-123", intent.getStringExtra(BaridiMobPaymentProcessor.EXTRA_ORDER_REFERENCE))
        assertEquals("The Illuminated Library", intent.getStringExtra(BaridiMobPaymentProcessor.EXTRA_RECIPIENT_NAME))
        assertTrue(intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }

    @Test
    fun `test Play Store and web fallback intent generation`() {
        val playStoreIntent = BaridiMobPaymentProcessor.createPlayStoreIntent()
        assertEquals(Intent.ACTION_VIEW, playStoreIntent.action)
        assertEquals(BaridiMobPaymentProcessor.URI_PLAY_STORE_MARKET, playStoreIntent.dataString)
        assertEquals("com.android.vending", playStoreIntent.`package`)

        val webIntent = BaridiMobPaymentProcessor.createWebFallbackIntent()
        assertEquals(Intent.ACTION_VIEW, webIntent.action)
        assertEquals(BaridiMobPaymentProcessor.URI_PLAY_STORE_WEB, webIntent.dataString)
    }

    @Test
    fun `test copying RIP to clipboard`() {
        val testRip = "00799999000987654321"
        val copied = BaridiMobPaymentProcessor.copyRipToClipboard(context, testRip)
        assertTrue(copied)

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val primaryClip = clipboard.primaryClip
        assertNotNull(primaryClip)
        assertEquals(testRip, primaryClip?.getItemAt(0)?.text?.toString())
    }

    @Test
    fun `test initiate payment returns store fallback when BaridiMob is uninstalled`() {
        val payment = PaymentMethod.BaridiMob(
            ripNumber = "00799999000123456789",
            accountHolder = "المكتبة المنيرة",
            amountDzd = 2000.0,
            orderReference = "TEST-REF"
        )

        val result = BaridiMobPaymentProcessor.initiatePayment(context, payment, autoCopyRip = true)

        // On Robolectric standard environment, dz.poste.baridimob is not installed
        assertTrue(result is BaridiMobPaymentProcessor.BaridiMobLaunchResult.StoreOpened)
        val storeResult = result as BaridiMobPaymentProcessor.BaridiMobLaunchResult.StoreOpened
        assertTrue(storeResult.ripCopied)
        assertNotNull(storeResult.intent)
    }
}

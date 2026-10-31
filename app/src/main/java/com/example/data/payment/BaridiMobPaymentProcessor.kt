package com.example.data.payment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log

private const val TAG = "BaridiMobProcessor"

/**
 * Processor helper for preparing, resolving, and initiating external BaridiMob transactions
 * for Algerian users via Algérie Poste.
 *
 * Implements:
 * - Package detection for `dz.poste.baridimob`
 * - Construction of deep launch intents with structured transaction extras (RIP, amount, reference)
 * - Automatic clipboard buffer management for immediate paste in BaridiMob's transfer view
 * - Graceful fallback to Google Play Store / Web browser when BaridiMob is not yet installed
 */
object BaridiMobPaymentProcessor {

    /**
     * Official application package identifier for BaridiMob (Algérie Poste).
     */
    const val PACKAGE_BARIDIMOB = "dz.poste.baridimob"

    /**
     * Google Play Store market intent URI.
     */
    const val URI_PLAY_STORE_MARKET = "market://details?id=$PACKAGE_BARIDIMOB"

    /**
     * HTTPS fallback URL for Google Play Store web interface.
     */
    const val URI_PLAY_STORE_WEB = "https://play.google.com/store/apps/details?id=$PACKAGE_BARIDIMOB"

    // Standardized Intent Extras for external payment routing
    const val EXTRA_RIP = "dz.poste.baridimob.extra.RIP"
    const val EXTRA_AMOUNT = "dz.poste.baridimob.extra.AMOUNT"
    const val EXTRA_RECIPIENT_NAME = "dz.poste.baridimob.extra.RECIPIENT_NAME"
    const val EXTRA_ORDER_REFERENCE = "dz.poste.baridimob.extra.ORDER_REFERENCE"

    // Generic aliases for interoperability
    const val EXTRA_ALIAS_RIP = "rip"
    const val EXTRA_ALIAS_AMOUNT = "amount"
    const val EXTRA_ALIAS_REFERENCE = "reference"

    /**
     * Result structure representing the outcome of a BaridiMob initiation request.
     */
    sealed class BaridiMobLaunchResult {
        abstract val isSuccess: Boolean
        abstract val message: String

        data class AppLaunched(
            val intent: Intent,
            val ripCopied: Boolean,
            override val message: String = "تم فتح تطبيق بريدي موب ونسخ رقم الحساب البريدي (RIP) بنجاح."
        ) : BaridiMobLaunchResult() {
            override val isSuccess: Boolean = true
        }

        data class StoreOpened(
            val intent: Intent,
            val ripCopied: Boolean,
            override val message: String = "تطبيق بريدي موب غير مثبت. تم توجيهك إلى متجر التطبيقات ونسخ رقم RIP."
        ) : BaridiMobLaunchResult() {
            override val isSuccess: Boolean = true
        }

        data class Failure(
            override val message: String,
            val throwable: Throwable? = null
        ) : BaridiMobLaunchResult() {
            override val isSuccess: Boolean = false
        }
    }

    /**
     * Checks if the official BaridiMob application is currently installed on the user's device.
     */
    fun isBaridiMobInstalled(context: Context): Boolean {
        return try {
            val packageManager = context.packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    PACKAGE_BARIDIMOB,
                    PackageManager.PackageInfoFlags.of(0L)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(PACKAGE_BARIDIMOB, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        } catch (e: Exception) {
            Log.w(TAG, "Error checking BaridiMob installation state: ${e.message}")
            false
        }
    }

    /**
     * Builds the Intent structure configured to launch BaridiMob with all transfer details.
     *
     * @param context Android context
     * @param payment The BaridiMob payment parameters (RIP, amount, reference, account holder)
     * @return The configured Intent or null if launch intent cannot be created
     */
    fun createLaunchIntent(context: Context, payment: PaymentMethod.BaridiMob): Intent? {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(PACKAGE_BARIDIMOB)
            ?: Intent(Intent.ACTION_MAIN).apply {
                setPackage(PACKAGE_BARIDIMOB)
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

        return configureIntentExtras(launchIntent, payment)
    }

    /**
     * Builds a standalone explicit Intent targeting the BaridiMob package with transaction extras.
     */
    fun createExplicitIntent(payment: PaymentMethod.BaridiMob): Intent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setPackage(PACKAGE_BARIDIMOB)
        }
        return configureIntentExtras(intent, payment)
    }

    /**
     * Attaches standardized payment extras and clip data to any intent.
     */
    fun configureIntentExtras(intent: Intent, payment: PaymentMethod.BaridiMob): Intent {
        return intent.apply {
            putExtra(EXTRA_RIP, payment.ripNumber)
            putExtra(EXTRA_AMOUNT, payment.amountDzd)
            putExtra(EXTRA_RECIPIENT_NAME, payment.accountHolder)
            putExtra(EXTRA_ORDER_REFERENCE, payment.orderReference)

            // Interoperability extras
            putExtra(EXTRA_ALIAS_RIP, payment.ripNumber)
            putExtra(EXTRA_ALIAS_AMOUNT, payment.amountDzd)
            putExtra(EXTRA_ALIAS_REFERENCE, payment.orderReference)
            putExtra(Intent.EXTRA_TEXT, payment.toShareableSummary())

            // Flags for standalone activity launch
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
    }

    /**
     * Generates a Google Play Store intent pointing to BaridiMob.
     */
    fun createPlayStoreIntent(): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(URI_PLAY_STORE_MARKET)).apply {
            setPackage("com.android.vending")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * Generates an HTTPS web intent pointing to BaridiMob on the Play Store web page.
     */
    fun createWebFallbackIntent(): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(URI_PLAY_STORE_WEB)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * Copies the RIP number to the Android clipboard buffer.
     */
    fun copyRipToClipboard(context: Context, rip: String, label: String = "رقم RIP بريدي موب"): Boolean {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (clipboard != null) {
                val clip = ClipData.newPlainText(label, rip)
                clipboard.setPrimaryClip(clip)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy RIP to clipboard", e)
            false
        }
    }

    /**
     * Prepares and resolves the appropriate intent for BaridiMob payment:
     * 1. Copies RIP to clipboard (if requested)
     * 2. Checks if BaridiMob is installed
     * 3. Returns AppLaunched result if installed, or StoreOpened result for fallback
     */
    fun initiatePayment(
        context: Context,
        payment: PaymentMethod.BaridiMob,
        autoCopyRip: Boolean = true
    ): BaridiMobLaunchResult {
        val ripCopied = if (autoCopyRip) {
            copyRipToClipboard(context, payment.ripNumber)
        } else {
            false
        }

        return if (isBaridiMobInstalled(context)) {
            val appIntent = createLaunchIntent(context, payment)
            if (appIntent != null) {
                BaridiMobLaunchResult.AppLaunched(appIntent, ripCopied)
            } else {
                // If package manager couldn't build launch intent, try explicit intent
                val explicitIntent = createExplicitIntent(payment)
                BaridiMobLaunchResult.AppLaunched(explicitIntent, ripCopied)
            }
        } else {
            // Check if Play Store app is available
            val storeIntent = createPlayStoreIntent()
            val resolved = storeIntent.resolveActivity(context.packageManager) != null
            val fallbackIntent = if (resolved) storeIntent else createWebFallbackIntent()
            BaridiMobLaunchResult.StoreOpened(fallbackIntent, ripCopied)
        }
    }

    /**
     * Initiates and starts the Activity for the BaridiMob transaction.
     * Safely executes `startActivity` and returns the launch result.
     */
    fun launchTransaction(
        context: Context,
        payment: PaymentMethod.BaridiMob,
        autoCopyRip: Boolean = true
    ): BaridiMobLaunchResult {
        val result = initiatePayment(context, payment, autoCopyRip)

        return try {
            when (result) {
                is BaridiMobLaunchResult.AppLaunched -> {
                    context.startActivity(result.intent)
                    result
                }
                is BaridiMobLaunchResult.StoreOpened -> {
                    try {
                        context.startActivity(result.intent)
                    } catch (_: Exception) {
                        // Fallback to web browser if market:// fails
                        context.startActivity(createWebFallbackIntent())
                    }
                    result
                }
                is BaridiMobLaunchResult.Failure -> result
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start BaridiMob activity", e)
            BaridiMobLaunchResult.Failure(
                message = "تعذر فتح تطبيق بريدي موب: ${e.localizedMessage ?: "خطأ غير معروف"}",
                throwable = e
            )
        }
    }
}

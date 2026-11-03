package com.example.util

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private fun calculateDeviceOffset(): String {
    val tz = TimeZone.getDefault()
    val offsetHours = tz.rawOffset / (1000 * 60 * 60)
    return if (offsetHours >= 0) "GMT+$offsetHours" else "GMT$offsetHours"
}

/**
 * Supported timezones for Algerian users (BaridiMob), International buyers (Binance UTC),
 * and custom global timezones.
 */
enum class AppTimezone(
    val id: String,
    val zoneId: String,
    val displayNameAr: String,
    val flag: String,
    val offsetLabel: String,
    val description: String
) {
    ALGIERS(
        id = "ALGIERS",
        zoneId = "Africa/Algiers",
        displayNameAr = "توقيت الجزائر",
        flag = "🇩🇿",
        offsetLabel = "GMT+1",
        description = "المنطقة الرسمية لخدمات بريدي موب والتعاملات في الجزائر"
    ),
    DEVICE(
        id = "DEVICE",
        zoneId = TimeZone.getDefault().id,
        displayNameAr = "توقيت الجهاز المحلي",
        flag = "📱",
        offsetLabel = calculateDeviceOffset(),
        description = "التوقيت الحالي المضبوط في نظام هاتفك"
    ),
    UTC(
        id = "UTC",
        zoneId = "UTC",
        displayNameAr = "التوقيت العالمي الموحد",
        flag = "🌐",
        offsetLabel = "UTC / GMT",
        description = "المعيار العالمي لمنصة Binance والمدفوعات الدولية"
    ),
    MECCA(
        id = "MECCA",
        zoneId = "Asia/Riyadh",
        displayNameAr = "توقيت مكة المكرمة",
        flag = "🕋",
        offsetLabel = "GMT+3",
        description = "توقيت المملكة العربية السعودية ودول الخليج العربي"
    ),
    PARIS(
        id = "PARIS",
        zoneId = "Europe/Paris",
        displayNameAr = "توقيت باريس وأوروبا",
        flag = "🇪🇺",
        offsetLabel = "GMT+1",
        description = "توقيت فرنسا والجالية في أوروبا"
    ),
    LONDON(
        id = "LONDON",
        zoneId = "Europe/London",
        displayNameAr = "توقيت لندن (غرينتش)",
        flag = "🇬🇧",
        offsetLabel = "GMT",
        description = "توقيت المملكة المتحدة وغرينتش"
    ),
    NEW_YORK(
        id = "NEW_YORK",
        zoneId = "America/New_York",
        displayNameAr = "توقيت نيويورك",
        flag = "🇺🇸",
        offsetLabel = "EST / EDT",
        description = "توقيت شرق الولايات المتحدة وكندا"
    ),
    DUBAI(
        id = "DUBAI",
        zoneId = "Asia/Dubai",
        displayNameAr = "توقيت دبي",
        flag = "🇦🇪",
        offsetLabel = "GMT+4",
        description = "توقيت الإمارات العربية المتحدة"
    );

    val timeZone: TimeZone
        get() = if (this == DEVICE) TimeZone.getDefault() else TimeZone.getTimeZone(zoneId)

    companion object {
        fun formatCurrentDeviceOffset(): String {
            val tz = TimeZone.getDefault()
            val offsetHours = tz.rawOffset / (1000 * 60 * 60)
            return if (offsetHours >= 0) "GMT+$offsetHours" else "GMT$offsetHours"
        }

        fun fromId(id: String?): AppTimezone {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: ALGIERS
        }
    }
}

/**
 * Utility functions for timezone-adjusted date & time formatting.
 */
object TimezoneHelper {

    private const val PREFS_NAME = "illuminated_timezone_prefs"
    private const val KEY_TIMEZONE_ID = "selected_timezone_id"

    fun getSavedTimezone(context: Context): AppTimezone {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val id = prefs.getString(KEY_TIMEZONE_ID, AppTimezone.ALGIERS.id)
        return AppTimezone.fromId(id)
    }

    fun saveTimezone(context: Context, timezone: AppTimezone) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TIMEZONE_ID, timezone.id).apply()
    }

    /**
     * Formats timestamp into Date + Time with explicit Timezone applied.
     * e.g.: "2026/10/31 14:30"
     */
    fun formatDateTime(timestamp: Long, timezone: AppTimezone): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).apply {
            timeZone = timezone.timeZone
        }
        return sdf.format(Date(timestamp))
    }

    /**
     * Formats timestamp with timezone name badge.
     * e.g.: "2026/10/31 14:30 (توقيت الجزائر GMT+1)"
     */
    fun formatFullDateTimeWithZone(timestamp: Long, timezone: AppTimezone): String {
        val formatted = formatDateTime(timestamp, timezone)
        return "$formatted (${timezone.displayNameAr} ${timezone.offsetLabel})"
    }

    /**
     * Formats Date only: "yyyy/MM/dd"
     */
    fun formatDateOnly(timestamp: Long, timezone: AppTimezone): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.US).apply {
            timeZone = timezone.timeZone
        }
        return sdf.format(Date(timestamp))
    }

    /**
     * Formats Time only: "HH:mm"
     */
    fun formatTimeOnly(timestamp: Long, timezone: AppTimezone): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.US).apply {
            timeZone = timezone.timeZone
        }
        return sdf.format(Date(timestamp))
    }

    /**
     * Formats Arabic localized date for human reading.
     * e.g.: "31 أكتوبر 2026 - 14:30"
     */
    fun formatArabicHumanDate(timestamp: Long, timezone: AppTimezone): String {
        val sdf = SimpleDateFormat("d MMMM yyyy - HH:mm", Locale("ar")).apply {
            timeZone = timezone.timeZone
        }
        return sdf.format(Date(timestamp))
    }

    /**
     * Returns current live time formatted in the given timezone.
     */
    fun getCurrentTimeFormatted(timezone: AppTimezone): String {
        return formatDateTime(System.currentTimeMillis(), timezone)
    }
}

package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.TimezoneSelectorDialog
import com.example.util.AppTimezone
import com.example.util.TimezoneHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w800dp-h2400dp")
class TimezoneHelperTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun `test AppTimezone fromId resolves correctly and defaults to ALGIERS`() {
        assertEquals(AppTimezone.ALGIERS, AppTimezone.fromId("ALGIERS"))
        assertEquals(AppTimezone.UTC, AppTimezone.fromId("utc"))
        assertEquals(AppTimezone.MECCA, AppTimezone.fromId("MECCA"))
        assertEquals(AppTimezone.DEVICE, AppTimezone.fromId("device"))
        assertEquals(AppTimezone.ALGIERS, AppTimezone.fromId("NON_EXISTING"))
        assertEquals(AppTimezone.ALGIERS, AppTimezone.fromId(null))
    }

    @Test
    fun `test TimezoneHelper save and get saved timezone`() {
        TimezoneHelper.saveTimezone(context, AppTimezone.UTC)
        assertEquals(AppTimezone.UTC, TimezoneHelper.getSavedTimezone(context))

        TimezoneHelper.saveTimezone(context, AppTimezone.MECCA)
        assertEquals(AppTimezone.MECCA, TimezoneHelper.getSavedTimezone(context))

        TimezoneHelper.saveTimezone(context, AppTimezone.ALGIERS)
        assertEquals(AppTimezone.ALGIERS, TimezoneHelper.getSavedTimezone(context))
    }

    @Test
    fun `test timezone formatting difference between UTC and Algiers`() {
        // Fixed epoch: 2026-10-31 12:00:00 UTC
        val calendarUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.OCTOBER)
            set(Calendar.DAY_OF_MONTH, 31)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val timestamp = calendarUtc.timeInMillis

        val formattedUtc = TimezoneHelper.formatDateTime(timestamp, AppTimezone.UTC)
        val formattedAlgiers = TimezoneHelper.formatDateTime(timestamp, AppTimezone.ALGIERS)
        val formattedMecca = TimezoneHelper.formatDateTime(timestamp, AppTimezone.MECCA)

        // UTC should show 12:00
        assertTrue(formattedUtc.contains("12:00"))
        // Algiers (GMT+1) should show 13:00
        assertTrue(formattedAlgiers.contains("13:00"))
        // Mecca (GMT+3) should show 15:00
        assertTrue(formattedMecca.contains("15:00"))
    }

    @Test
    fun `test formatFullDateTimeWithZone includes timezone label`() {
        val now = System.currentTimeMillis()
        val formatted = TimezoneHelper.formatFullDateTimeWithZone(now, AppTimezone.ALGIERS)
        assertTrue(formatted.contains("توقيت الجزائر"))
        assertTrue(formatted.contains("GMT+1"))
    }

    @Test
    fun `test formatDateOnly and formatTimeOnly`() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Africa/Algiers")).apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.OCTOBER)
            set(Calendar.DAY_OF_MONTH, 31)
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 45)
            set(Calendar.SECOND, 0)
        }
        val timestamp = calendar.timeInMillis

        val dateOnly = TimezoneHelper.formatDateOnly(timestamp, AppTimezone.ALGIERS)
        val timeOnly = TimezoneHelper.formatTimeOnly(timestamp, AppTimezone.ALGIERS)

        assertEquals("2026/10/31", dateOnly)
        assertEquals("18:45", timeOnly)
    }

    @Test
    fun `test TimezoneSelectorDialog displays all timezones and selects on click`() {
        var selectedTz: AppTimezone? = null
        var dismissed = false

        composeTestRule.setContent {
            TimezoneSelectorDialog(
                currentTimezone = AppTimezone.ALGIERS,
                onSelectTimezone = { selectedTz = it },
                onDismiss = { dismissed = true }
            )
        }

        composeTestRule.onNodeWithTag("timezone_selector_dialog").assertExists()
        composeTestRule.onNodeWithTag("timezone_item_ALGIERS").assertExists()
        composeTestRule.onNodeWithTag("timezone_item_DEVICE").assertExists()
        composeTestRule.onNodeWithTag("timezone_item_UTC").assertExists()

        // Click UTC option
        composeTestRule.onNodeWithTag("timezone_item_UTC").performClick()
        // Click Apply button
        composeTestRule.onNodeWithTag("apply_timezone_btn").performClick()

        assertEquals(AppTimezone.UTC, selectedTz)
        assertTrue(dismissed)
    }
}

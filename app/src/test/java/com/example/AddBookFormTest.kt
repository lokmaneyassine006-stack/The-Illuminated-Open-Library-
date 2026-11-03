package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import com.example.data.auth.AuthUser
import com.example.data.model.Book
import com.example.ui.components.AddBookDialog
import com.example.ui.components.AddBookFormContent
import com.example.ui.components.BookPaymentMethodOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w800dp-h2400dp")
class AddBookFormTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `test AddBookFormContent renders all required fields including payment methods BaridiMob and Binance`() {
        var savedBook: Book? = null
        var dismissed = false

        composeTestRule.setContent {
            Box(
                modifier = Modifier.size(800.dp, 1200.dp)
            ) {
                AddBookFormContent(
                    initialBook = null,
                    authUser = AuthUser(uid = "author_1", displayName = "أحمد الجزائري", email = "ahmed@algeria.dz"),
                    isSaving = false,
                    isBottomSheet = false,
                    onDismiss = { dismissed = true },
                    onSave = { savedBook = it }
                )
            }
        }

        // 1. Verify container and required form fields exist
        composeTestRule.onNodeWithTag("add_book_form_container").assertExists()
        composeTestRule.onNodeWithTag("add_book_title_field").assertExists()
        composeTestRule.onNodeWithTag("add_book_author_field").assertExists()
        composeTestRule.onNodeWithTag("add_book_description_field").assertExists()
        composeTestRule.onNodeWithTag("add_book_price_field").assertExists()

        // 2. Verify payment method options exist (BaridiMob and Binance)
        composeTestRule.onNodeWithTag("add_book_payment_method_baridimob").assertExists()
        composeTestRule.onNodeWithTag("add_book_payment_method_binance").assertExists()
        composeTestRule.onNodeWithTag("add_book_payment_method_both").assertExists()

        // 3. Fill in book details
        composeTestRule.onNodeWithTag("add_book_title_field").performTextInput("رواية شمس الأوراس")
        composeTestRule.onNodeWithTag("add_book_description_field").performTextInput("رواية تاريخية أدبية من التراث الثوري الجزائري")

        // 4. Select Binance payment method
        composeTestRule.onNodeWithTag("add_book_payment_method_binance").performClick()

        // 5. Submit book
        composeTestRule.onNodeWithTag("add_book_submit_button").performClick()

        assertNotNull(savedBook)
        assertEquals("رواية شمس الأوراس", savedBook?.title)
        assertEquals("أحمد الجزائري", savedBook?.author)
        assertEquals("رواية تاريخية أدبية من التراث الثوري الجزائري", savedBook?.description)
        assertTrue("Price in USDT should be positive", savedBook!!.priceUsdt > 0.0)
    }

    @Test
    fun `test AddBookFormContent with BaridiMob payment method submits correct DZD pricing`() {
        var savedBook: Book? = null

        composeTestRule.setContent {
            Box(
                modifier = Modifier.size(800.dp, 1200.dp)
            ) {
                AddBookFormContent(
                    initialBook = null,
                    authUser = AuthUser(uid = "author_2", displayName = "مولود بن سالم", email = "mouloud@algeria.dz"),
                    isSaving = false,
                    isBottomSheet = true,
                    onDismiss = {},
                    onSave = { savedBook = it }
                )
            }
        }

        // Fill required fields
        composeTestRule.onNodeWithTag("add_book_title_field").performTextInput("تاريخ الجزائر القديم")

        // Explicitly select BaridiMob
        composeTestRule.onNodeWithTag("add_book_payment_method_baridimob").performClick()

        // Submit form
        composeTestRule.onNodeWithTag("add_book_submit_button").performClick()

        assertNotNull(savedBook)
        assertEquals("تاريخ الجزائر القديم", savedBook?.title)
        assertEquals("مولود بن سالم", savedBook?.author)
        assertEquals(1200.0, savedBook!!.priceDzd, 0.01)
        assertEquals(false, savedBook!!.isFree)
    }

    @Test
    fun `test AddBookFormContent free book toggle sets isFree to true and zeroes price`() {
        var savedBook: Book? = null

        composeTestRule.setContent {
            Box(
                modifier = Modifier.size(800.dp, 1200.dp)
            ) {
                AddBookFormContent(
                    initialBook = null,
                    authUser = null,
                    isSaving = false,
                    onDismiss = {},
                    onSave = { savedBook = it }
                )
            }
        }

        composeTestRule.onNodeWithTag("add_book_title_field").performTextInput("كتاب مجاني للمبتدئين")
        composeTestRule.onNodeWithTag("add_book_is_free_switch").performClick()
        composeTestRule.onNodeWithTag("add_book_submit_button").performClick()

        assertNotNull(savedBook)
        assertTrue(savedBook!!.isFree)
        assertEquals(0.0, savedBook!!.priceDzd, 0.01)
        assertEquals(0.0, savedBook!!.priceUsdt, 0.01)
    }

    @Test
    fun `test AddBookDialog cancel button triggers onDismiss`() {
        var dismissed = false

        composeTestRule.setContent {
            AddBookDialog(
                initialBook = null,
                onDismiss = { dismissed = true },
                onSave = {}
            )
        }

        composeTestRule.onNodeWithTag("add_book_dialog").assertExists()
        composeTestRule.onNodeWithTag("add_book_cancel_button").performClick()
        assertTrue(dismissed)
    }
}

package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import com.example.data.auth.AuthUser
import com.example.data.model.Book
import com.example.ui.components.AuthorBookUploadForm
import com.example.ui.components.BookCategories
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
class AuthorBookUploadFormTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `test Book data class supports cover image and dual pricing`() {
        val book = Book(
            id = 42L,
            title = "أسرار المكتبة المنيرة",
            author = "الكاتب الجزائري",
            category = "فكر وفلسفة",
            description = "كتاب جامع ومفصل عن آفاق المعرفة الرقمية",
            coverImageUrl = "https://images.example.com/cover.jpg",
            coverStyleIndex = 2,
            isFree = false,
            priceDzd = 1500.0,
            priceUsdt = 7.5,
            publishedPeriod = "2026"
        )

        assertEquals(42L, book.id)
        assertEquals("أسرار المكتبة المنيرة", book.title)
        assertEquals("الكاتب الجزائري", book.author)
        assertEquals("https://images.example.com/cover.jpg", book.coverImageUrl)
        assertEquals(1500.0, book.priceDzd, 0.01)
        assertEquals(7.5, book.priceUsdt, 0.01)
        assertEquals(false, book.isFree)
        assertTrue(BookCategories.contains(book.category))
    }

    @Test
    fun `test AuthorBookUploadForm renders fields and handles save callback`() {
        var savedBook: Book? = null

        val authUser = AuthUser(
            uid = "author_uid_123",
            displayName = "أحمد البشير",
            email = "ahmed@example.com",
            photoUrl = null
        )

        val initialBook = Book(
            title = "رواية الأندلس المفقودة",
            author = "أحمد البشير",
            description = "رحلة تاريخية مشوقة"
        )

        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .size(800.dp, 1000.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AuthorBookUploadForm(
                    initialBook = initialBook,
                    authUser = authUser,
                    isSaving = false,
                    onDismiss = {},
                    onSaveBookToFirestore = { book ->
                        savedBook = book
                    }
                )
            }
        }

        // Verify key input elements are rendered in semantic tree
        composeTestRule.onNodeWithTag("author_book_upload_form").assertExists()
        composeTestRule.onNodeWithTag("author_form_title_input").assertExists()
        composeTestRule.onNodeWithTag("author_form_author_input").assertExists()
        composeTestRule.onNodeWithTag("author_form_desc_input").assertExists()
        composeTestRule.onNodeWithTag("author_form_save_btn").assertExists()

        // Scroll to save button and click
        composeTestRule.onNodeWithTag("author_form_save_btn")
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Verify callback is triggered with correct fields
        assertNotNull(savedBook)
        assertEquals("رواية الأندلس المفقودة", savedBook?.title)
        assertEquals("أحمد البشير", savedBook?.author)
        assertEquals("رحلة تاريخية مشوقة", savedBook?.description)
    }
}

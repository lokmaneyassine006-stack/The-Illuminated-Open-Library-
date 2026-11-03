package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.data.auth.AuthUser
import com.example.data.model.Book
import com.example.ui.components.AuthorLoginScreen
import com.example.ui.components.UserBookCollectionScreen
import com.example.ui.viewmodel.SalesSummary
import com.example.util.AppTimezone
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
class UserBookCollectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleBooks = listOf(
        Book(
            id = 1L,
            title = "أدب المقاومة الجزائرية",
            author = "مولود فرعون",
            category = "تاريخ وأدب",
            description = "كتاب أدبي توثيقي نادر من عيون الأدب الجزائري",
            pageCount = 240,
            isFree = false,
            priceDzd = 1800.0,
            priceUsdt = 9.0,
            publishedPeriod = "2026"
        ),
        Book(
            id = 2L,
            title = "مقدمة في الاقتصاد الرقمي والعملات المشفرة",
            author = "د. ياسين لقمان",
            category = "اقتصاد وتقنية",
            description = "دليل شامل للتعاملات المالية الرقمية عبر بينانس",
            pageCount = 180,
            isFree = true,
            priceDzd = 0.0,
            priceUsdt = 0.0,
            publishedPeriod = "2026"
        )
    )

    @Test
    fun `test UserBookCollectionScreen displays books from Room and Add Book FAB triggers callback`() {
        var addButtonClicked = false
        var editPriceBook: Book? = null
        var editBookClicked: Book? = null
        var deleteBookClicked: Book? = null
        var readBookClicked: Book? = null
        var backClicked = false

        composeTestRule.setContent {
            UserBookCollectionScreen(
                books = sampleBooks,
                timezone = AppTimezone.ALGIERS,
                onAddBookClick = { addButtonClicked = true },
                onEditPriceClick = { editPriceBook = it },
                onEditBookClick = { editBookClicked = it },
                onDeleteBookClick = { deleteBookClicked = it },
                onReadBookClick = { readBookClicked = it },
                onBackClick = { backClicked = true }
            )
        }

        // Screen and LazyColumn exist
        composeTestRule.onNodeWithTag("user_book_collection_screen").assertExists()
        composeTestRule.onNodeWithTag("book_collection_lazy_column").assertExists()

        // FAB exists and can be clicked
        composeTestRule.onNodeWithTag("add_book_fab").assertExists()
        composeTestRule.onNodeWithTag("add_book_fab").performClick()
        assertTrue("Add Book FAB should invoke onAddBookClick", addButtonClicked)

        // Books in LazyColumn are displayed
        composeTestRule.onNodeWithTag("collection_book_item_1").assertExists()
        composeTestRule.onNodeWithTag("collection_book_item_2").assertExists()

        // Action buttons on book 1
        composeTestRule.onNodeWithTag("collection_edit_price_1").performClick()
        assertEquals(1L, editPriceBook?.id)

        composeTestRule.onNodeWithTag("collection_edit_book_1").performClick()
        assertEquals(1L, editBookClicked?.id)

        composeTestRule.onNodeWithTag("collection_read_book_1").performClick()
        assertEquals(1L, readBookClicked?.id)

        composeTestRule.onNodeWithTag("collection_delete_book_1").performClick()
        assertEquals(1L, deleteBookClicked?.id)

        // Back button
        composeTestRule.onNodeWithTag("collection_back_button").performClick()
        assertTrue(backClicked)
    }

    @Test
    fun `test UserBookCollectionScreen empty state displayed when books list is empty`() {
        var addButtonClicked = false

        composeTestRule.setContent {
            UserBookCollectionScreen(
                books = emptyList(),
                timezone = AppTimezone.ALGIERS,
                onAddBookClick = { addButtonClicked = true },
                onEditPriceClick = {},
                onEditBookClick = {},
                onDeleteBookClick = {},
                onReadBookClick = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithTag("empty_collection_card").assertExists()
        composeTestRule.onNodeWithTag("add_book_fab").assertExists()
        composeTestRule.onNodeWithTag("empty_add_book_btn").performClick()
        assertTrue(addButtonClicked)
    }

    @Test
    fun `test top search bar filters LazyColumn collection by book title and author name`() {
        composeTestRule.setContent {
            UserBookCollectionScreen(
                books = sampleBooks,
                timezone = AppTimezone.ALGIERS,
                onAddBookClick = {},
                onEditPriceClick = {},
                onEditBookClick = {},
                onDeleteBookClick = {},
                onReadBookClick = {},
                onBackClick = {}
            )
        }

        // Search bar exists at top
        composeTestRule.onNodeWithTag("collection_search_bar").assertExists()
        composeTestRule.onNodeWithTag("collection_search_field").assertExists()

        // Initially both books exist
        composeTestRule.onNodeWithTag("collection_book_item_1").assertExists()
        composeTestRule.onNodeWithTag("collection_book_item_2").assertExists()

        // 1. Filter by book title
        composeTestRule.onNodeWithTag("collection_search_field").performTextInput("المقاومة")
        composeTestRule.onNodeWithTag("collection_book_item_1").assertExists()
        composeTestRule.onNodeWithTag("collection_book_item_2").assertDoesNotExist()

        // Clear query with clear button
        composeTestRule.onNodeWithTag("collection_search_clear_button").performClick()
        composeTestRule.onNodeWithTag("collection_book_item_1").assertExists()
        composeTestRule.onNodeWithTag("collection_book_item_2").assertExists()

        // 2. Filter by author name
        composeTestRule.onNodeWithTag("collection_search_field").performTextInput("لقمان")
        composeTestRule.onNodeWithTag("collection_book_item_2").assertExists()
        composeTestRule.onNodeWithTag("collection_book_item_1").assertDoesNotExist()

        // Clear query with clear button
        composeTestRule.onNodeWithTag("collection_search_clear_button").performClick()
        composeTestRule.onNodeWithTag("collection_book_item_1").assertExists()
        composeTestRule.onNodeWithTag("collection_book_item_2").assertExists()

        // 3. Filter with no match displays empty results state and clear button restores
        composeTestRule.onNodeWithTag("collection_search_field").performTextInput("غير_موجود")
        composeTestRule.onNodeWithTag("empty_collection_card").assertExists()
        composeTestRule.onNodeWithText("لا توجد نتائج مطابقة لبحثك").assertExists()
        composeTestRule.onNodeWithTag("empty_clear_search_btn").performClick()
        composeTestRule.onNodeWithTag("collection_book_item_1").assertExists()
        composeTestRule.onNodeWithTag("collection_book_item_2").assertExists()
    }

    @Test
    fun `test AuthorLoginScreen unauthenticated state has Google Sign-In button`() {
        var signInTriggered = false
        var backTriggered = false

        composeTestRule.setContent {
            AuthorLoginScreen(
                user = null,
                isLoading = false,
                errorMessage = null,
                allBooks = sampleBooks,
                salesSummary = SalesSummary(),
                timezone = AppTimezone.ALGIERS,
                onSignIn = { signInTriggered = true },
                onSignOut = {},
                onBackClick = { backTriggered = true },
                onOpenAddBook = {},
                onOpenDashboard = {},
                onOpenPaymentSettings = {}
            )
        }

        composeTestRule.onNodeWithTag("author_login_screen").assertExists()
        composeTestRule.onNodeWithTag("login_screen_google_btn").assertExists()
        composeTestRule.onNodeWithTag("login_guest_browse_btn").performClick()
        assertTrue(backTriggered)
    }

    @Test
    fun `test AuthorLoginScreen authenticated state displays author profile and actions`() {
        val testUser = AuthUser(
            uid = "author_12345",
            displayName = "الكاتب الجزائري",
            email = "author@algeria.dz"
        )

        var signOutTriggered = false
        var addBookTriggered = false
        var dashboardTriggered = false

        composeTestRule.setContent {
            AuthorLoginScreen(
                user = testUser,
                isLoading = false,
                errorMessage = null,
                allBooks = sampleBooks,
                salesSummary = SalesSummary(totalDzd = 25000.0, totalUsdt = 120.0),
                timezone = AppTimezone.ALGIERS,
                onSignIn = {},
                onSignOut = { signOutTriggered = true },
                onBackClick = {},
                onOpenAddBook = { addBookTriggered = true },
                onOpenDashboard = { dashboardTriggered = true },
                onOpenPaymentSettings = {}
            )
        }

        composeTestRule.onNodeWithTag("author_profile_card").assertExists()
        composeTestRule.onNodeWithText("الكاتب الجزائري").assertExists()

        // Quick action buttons
        composeTestRule.onNodeWithTag("author_add_book_btn").performClick()
        assertTrue(addBookTriggered)

        composeTestRule.onNodeWithTag("author_dashboard_btn").performClick()
        assertTrue(dashboardTriggered)

        // Sign out
        composeTestRule.onNodeWithTag("author_sign_out_btn").performClick()
        assertTrue(signOutTriggered)
    }
}

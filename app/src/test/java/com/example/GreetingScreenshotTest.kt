package com.example

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.data.model.Book
import com.example.ui.components.BookCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleBook = Book(
      title = "أنوار الحكمة والمعرفة الرقمية",
      author = "المؤلف",
      category = "فكر وفلسفة",
      description = "دراسة معمقة في تقاطع الفكر الإنساني مع عصر المعلومات.",
      priceDzd = 1200.0,
      priceUsdt = 6.0,
      publishedPeriod = "2026"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        BookCard(
          book = sampleBook,
          onReadClick = {},
          onBuyClick = {},
          onEditPriceClick = {},
          onEditBookClick = {},
          onDeleteClick = {},
          modifier = Modifier.padding(16.dp)
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}


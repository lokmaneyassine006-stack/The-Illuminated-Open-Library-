package com.example.data.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "GeminiAiService"

class GeminiAiService {
    // Lazily initialize generative model with gemini-3.8-flash per Firebase AI Logic skill
    private val model: GenerativeModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    /**
     * Ask Gemini a question or request a summary about a book with streaming text chunks.
     */
    fun askBookAssistantStream(bookTitle: String, author: String, content: String, userQuestion: String): Flow<String> = flow {
        val prompt = """
            أنت مساعد القراءة والتحليل الأدبي والفكري في تطبيق "The Illuminated Open Library".
            
            معلومات الكتاب:
            - العنوان: $bookTitle
            - الكاتب: $author
            - مقتطف أو محتوى الكتاب:
            $content
            
            سؤال أو طلب القارئ:
            $userQuestion
            
            يرجى الإجابة باللغة العربية بأسلوب راقٍ، ثري، ومنسق بنقاط واضحة ومباشرة.
        """.trimIndent()

        try {
            withTimeout(60_000L) {
                model.generateContentStream(prompt).collect { chunk ->
                    val text = chunk.text.orEmpty()
                    if (text.isNotEmpty()) {
                        emit(text)
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Streaming timed out", e)
            emit("\n[انتهت مهلة الاتصال بالذكاء الاصطناعي (60 ثانية). يرجى إعادة المحاولة.]")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Streaming generation failed", e)
            emit("\n[تعذر الحصول على الرد من Gemini: ${e.localizedMessage ?: "حدث خطأ غير متوقع"}]")
        }
    }

    /**
     * Generate book details (description, sample chapter, suggested category, pricing) for authors.
     */
    suspend fun generateBookDraft(titleOrTopic: String): GeneratedBookDraft {
        val prompt = """
            أنت خبير نشر وتأليف ومساعد ذكي في تطبيق "The Illuminated Open Library".
            المؤلف يرغب في إضافة كتاب بعنوان أو فكرة: "$titleOrTopic".
            
            المطلوب:
            قم بإنشاء مسودة متكاملة لهذا الكتاب باللغة العربية بالصيغة المحددة أدناه بدقة، مع الحفاظ على الفواصل الثلاثية التالية:
            
            ===DESCRIPTION===
            (اكتب هنا وصفاً تشويقياً وأدبياً أو علمياً معمقاً للكتاب في 2-3 فقرات)
            ===SAMPLE===
            (اكتب هنا عينة مجانية للقراءة أو مقدمة وفصلاً أول مشوقاً من حوالي 200 إلى 300 كلمة)
            ===CATEGORY===
            (اختر أحد التصنيفات التالية فقط: فكر وفلسفة، أدب وروايات، تقنية وبرمجة، تاريخ وتراث، علوم ومعرفة، اقتصاد ومالية، تطوير الذات، شعر وبلاغة)
            ===PAGES===
            (عدد الصفحات التقريبي كرقم صحيح فقط مثلا 180)
            ===PRICE_DZD===
            (سعر مقترح بالدينار الجزائري لبريدي موب كرقم صحيح مثلا 1200)
            ===PRICE_USDT===
            (سعر مقترح بالدولار الرقمي USDT لبينانس كرقم عشري مثلا 6.0)
        """.trimIndent()

        return try {
            val response = withTimeout(60_000L) {
                model.generateContent(prompt)
            }
            val text = response.text.orEmpty()
            parseGeneratedDraft(text)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Draft generation timed out", e)
            throw IllegalStateException("استغرقت العملية أكثر من 60 ثانية، يرجى المحاولة مرة أخرى.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate book draft", e)
            throw e
        }
    }

    private fun parseGeneratedDraft(raw: String): GeneratedBookDraft {
        fun extractSection(startTag: String, nextTags: List<String>): String {
            val startIndex = raw.indexOf(startTag)
            if (startIndex == -1) return ""
            val contentStart = startIndex + startTag.length
            val nextIndex = nextTags
                .map { raw.indexOf(it, contentStart) }
                .filter { it != -1 }
                .minOrNull() ?: raw.length
            return raw.substring(contentStart, nextIndex).trim()
        }

        val description = extractSection("===DESCRIPTION===", listOf("===SAMPLE===", "===CATEGORY==="))
        val sample = extractSection("===SAMPLE===", listOf("===CATEGORY===", "===PAGES==="))
        val category = extractSection("===CATEGORY===", listOf("===PAGES===", "===PRICE_DZD==="))
            .lines().firstOrNull()?.trim() ?: "فكر وفلسفة"
        val pagesStr = extractSection("===PAGES===", listOf("===PRICE_DZD===", "===PRICE_USDT==="))
            .filter { it.isDigit() }
        val priceDzdStr = extractSection("===PRICE_DZD===", listOf("===PRICE_USDT==="))
            .filter { it.isDigit() }
        val priceUsdtStr = extractSection("===PRICE_USDT===", emptyList())
            .filter { it.isDigit() || it == '.' }

        return GeneratedBookDraft(
            description = description.ifBlank { raw },
            sampleContent = sample,
            category = category.ifBlank { "فكر وفلسفة" },
            pageCount = pagesStr.toIntOrNull() ?: 150,
            priceDzd = priceDzdStr.toDoubleOrNull() ?: 1000.0,
            priceUsdt = priceUsdtStr.toDoubleOrNull() ?: 5.0
        )
    }
}

data class GeneratedBookDraft(
    val description: String,
    val sampleContent: String,
    val category: String,
    val pageCount: Int,
    val priceDzd: Double,
    val priceUsdt: Double
)

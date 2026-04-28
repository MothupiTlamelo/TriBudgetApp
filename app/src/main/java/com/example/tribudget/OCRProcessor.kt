package com.example.tribudget

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

data class ReceiptData(
    val amount: Double?,
    val date: String?,
    val merchant: String?,
    val confidence: Float
)

@Suppress("unused")
class OCRProcessor {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun processReceipt(imageUri: Uri, context: Context): ReceiptData {
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            val result = recognizer.process(image).getResult()
            val text = result.text

            val amount = extractAmount(text)
            val date = extractDate(text)
            val merchant = extractMerchant(text)

            ReceiptData(
                amount = amount,
                date = date,
                merchant = merchant,
                confidence = calculateConfidence(amount, date, merchant)
            )
        } catch (@Suppress("UNUSED_PARAMETER") exception: Exception) {
            ReceiptData(null, null, null, 0f)
        }
    }

    private fun extractAmount(text: String): Double? {
        val amountPatterns = listOf(
            Regex("""R\s?(\d+(?:[.,]\d{2})?)""", RegexOption.IGNORE_CASE),
            Regex("""(\d+(?:[.,]\d{2})?)\s?ZAR""", RegexOption.IGNORE_CASE),
            Regex("""TOTAL[\s:]*R?\s?(\d+(?:[.,]\d{2})?)""", RegexOption.IGNORE_CASE),
            Regex("""AMOUNT[\s:]*R?\s?(\d+(?:[.,]\d{2})?)""", RegexOption.IGNORE_CASE),
            Regex("""DUE[\s:]*R?\s?(\d+(?:[.,]\d{2})?)""", RegexOption.IGNORE_CASE)
        )

        for (pattern in amountPatterns) {
            val match = pattern.find(text)
            val amountStr = match?.groupValues?.getOrNull(1)
            if (amountStr != null) {
                return amountStr.replace(",", ".").toDoubleOrNull()
            }
        }
        return null
    }

    private fun extractDate(text: String): String? {
        val datePatterns = listOf(
            Regex("""(\d{4}-\d{2}-\d{2})"""),
            Regex("""(\d{2}/\d{2}/\d{4})"""),
            Regex("""(\d{2}\.\d{2}\.\d{4})"""),
            Regex("""(\d{1,2}\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\s+\d{4})""", RegexOption.IGNORE_CASE)
        )

        for (pattern in datePatterns) {
            val match = pattern.find(text)
            val dateStr = match?.groupValues?.getOrNull(1)
            if (dateStr != null) {
                return dateStr
            }
        }
        return null
    }

    private fun extractMerchant(text: String): String? {
        val lines = text.lines()
            .filter { it.length in 5..50 && it.isNotBlank() }
            .filterNot { it.matches(Regex(".*\\d+.*")) }
        return lines.firstOrNull()?.trim()
    }

    private fun calculateConfidence(amount: Double?, date: String?, merchant: String?): Float {
        var confidence = 0f
        if (amount != null) confidence += 0.5f
        if (date != null) confidence += 0.3f
        if (merchant != null) confidence += 0.2f
        return confidence
    }
}
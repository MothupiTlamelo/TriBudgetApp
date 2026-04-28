package com.example.tribudget

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ReceiptScannerActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var btnCapture: Button
    private lateinit var btnFlash: Button
    private lateinit var tvInstruction: TextView
    private lateinit var progressBar: ProgressBar

    private var imageCapture: ImageCapture? = null
    private var cameraExecutor: ExecutorService? = null
    private var flashMode = ImageCapture.FLASH_MODE_AUTO

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission is required to scan receipts", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            processImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_scanner)

        initializeViews()
        setupClickListeners()
        checkCameraPermission()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun initializeViews() {
        previewView = findViewById(R.id.previewView)
        btnCapture = findViewById(R.id.btnCapture)
        btnFlash = findViewById(R.id.btnFlash)
        tvInstruction = findViewById(R.id.tvInstruction)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.btnClose).setOnClickListener {
            finish()
        }

        tvInstruction.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnCapture.setOnClickListener {
            takePhoto()
        }

        btnFlash.setOnClickListener {
            toggleFlash()
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            imageCapture = ImageCapture.Builder()
                .setFlashMode(flashMode)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createPhotoFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    processImage(savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@ReceiptScannerActivity, "Failed to capture: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun createPhotoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("RECEIPT_${timeStamp}_", ".jpg", storageDir)
    }

    private fun toggleFlash() {
        flashMode = when (flashMode) {
            ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
            else -> ImageCapture.FLASH_MODE_AUTO
        }

        imageCapture?.flashMode = flashMode

        val flashIcon = when (flashMode) {
            ImageCapture.FLASH_MODE_AUTO -> "⚡A"
            ImageCapture.FLASH_MODE_ON -> "⚡"
            else -> "⚡OFF"
        }
        btnFlash.text = flashIcon
    }

    private fun processImage(uri: Uri) {
        progressBar.visibility = ProgressBar.VISIBLE
        btnCapture.isEnabled = false

        Thread {
            try {
                val inputImage = InputImage.fromFilePath(this, uri)
                val result = textRecognizer.process(inputImage)
                val resultText = result.getResult().text

                val extractedData = extractReceiptData(resultText)

                runOnUiThread {
                    progressBar.visibility = ProgressBar.GONE
                    btnCapture.isEnabled = true
                    showExtractedDataDialog(extractedData, uri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    progressBar.visibility = ProgressBar.GONE
                    btnCapture.isEnabled = true
                    Toast.makeText(this, "Failed to process: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun extractReceiptData(text: String): ReceiptExtractedData {
        var amount: Double? = null
        var date: String? = null
        var merchant: String?
        var tax: Double?
        val items = mutableListOf<String>()

        // Extract amount - multiple patterns
        val amountPatterns = listOf(
            Regex("""TOTAL[\s:]*R?\s?(\d+(?:[.,]\d{2})?)""", RegexOption.IGNORE_CASE),
            Regex("""AMOUNT[\s:]*R?\s?(\d+(?:[.,]\d{2})?)""", RegexOption.IGNORE_CASE),
            Regex("""DUE[\s:]*R?\s?(\d+(?:[.,]\d{2})?)""", RegexOption.IGNORE_CASE),
            Regex("""R\s?(\d+(?:[.,]\d{2})?)\s*$""", RegexOption.MULTILINE),
            Regex("""(\d+(?:[.,]\d{2})?)\s*ZAR""", RegexOption.IGNORE_CASE)
        )

        for (pattern in amountPatterns) {
            val match = pattern.find(text)
            if (match != null) {
                amount = match.groupValues[1].replace(",", ".").toDoubleOrNull()
                if (amount != null) break
            }
        }

        // Extract date
        val datePatterns = listOf(
            Regex("""(\d{4}-\d{2}-\d{2})"""),
            Regex("""(\d{2}/\d{2}/\d{4})"""),
            Regex("""(\d{2}\.\d{2}\.\d{4})"""),
            Regex("""(\d{1,2}\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\s+\d{4})""", RegexOption.IGNORE_CASE)
        )

        for (pattern in datePatterns) {
            val match = pattern.find(text)
            if (match != null) {
                date = match.groupValues[1]
                break
            }
        }

        // Extract merchant (first few lines usually contain store name)
        val lines = text.lines().filter { it.length in 5..50 && it.isNotBlank() }
        merchant = lines.firstOrNull {
            !it.matches(Regex(".*\\d+.*")) && it.length < 40
        }

        // Extract tax if present
        val taxPattern = Regex("""TAX[\s:]*R?\s?(\d+(?:[.,]\d{2})?)""", RegexOption.IGNORE_CASE)
        val taxMatch = taxPattern.find(text)
        tax = taxMatch?.groupValues?.get(1)?.replace(",", ".")?.toDoubleOrNull()

        // Extract line items
        val itemPattern = Regex("""([A-Za-z\s]+)\s+(\d+(?:[.,]\d{2})?)""")
        items.addAll(itemPattern.findAll(text).map { match ->
            "${match.groupValues[1].trim()}: R${match.groupValues[2]}"
        }.take(10))

        return ReceiptExtractedData(
            amount = amount,
            date = date,
            merchant = merchant,
            tax = tax,
            items = items,
            confidence = calculateConfidence(amount, date, merchant)
        )
    }

    private fun calculateConfidence(amount: Double?, date: String?, merchant: String?): Int {
        var confidence = 0
        if (amount != null) confidence += 50
        if (date != null) confidence += 30
        if (merchant != null) confidence += 20
        return confidence
    }

    private fun showExtractedDataDialog(data: ReceiptExtractedData, receiptUri: Uri) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Receipt Data Extracted")

        val items = mutableListOf<String>()
        data.amount?.let { items.add(String.format(Locale.US, "💰 Amount: R%.2f", it)) }
        data.date?.let { items.add("📅 Date: $it") }
        data.merchant?.let { items.add("🏪 Store: $it") }
        data.tax?.let { items.add(String.format(Locale.US, "🧾 Tax: R%.2f", it)) }
        items.add("\nConfidence: ${data.confidence}%")

        if (data.items.isNotEmpty()) {
            items.add("\nItems:")
            items.addAll(data.items.take(5))
        }

        builder.setPositiveButton("Use This Data") { _, _ ->
            useExtractedData(data, receiptUri)
        }

        builder.setNegativeButton("Cancel") { _, _ -> }

        builder.setNeutralButton("Edit Manually") { _, _ ->
            startActivity(Intent(this, AddExpense::class.java))
        }

        builder.show()
    }

    private fun useExtractedData(data: ReceiptExtractedData, receiptUri: Uri) {
        val intent = Intent(this, AddExpense::class.java)

        data.amount?.let { intent.putExtra("EXTRACTED_AMOUNT", it) }
        data.date?.let { intent.putExtra("EXTRACTED_DATE", it) }
        data.merchant?.let { intent.putExtra("EXTRACTED_DESCRIPTION", it) }

        val savedPath = saveReceiptPhoto(receiptUri)
        intent.putExtra("EXTRACTED_PHOTO_PATH", savedPath)

        startActivity(intent)
        finish()
    }

    private fun saveReceiptPhoto(uri: Uri): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "RECEIPT_${timeStamp}.jpg"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val destFile = File(storageDir, fileName)

        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }

        return destFile.absolutePath
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor?.shutdown()
    }
}

data class ReceiptExtractedData(
    val amount: Double?,
    val date: String?,
    val merchant: String?,
    val tax: Double?,
    val items: List<String>,
    val confidence: Int
)
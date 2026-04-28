package com.example.tribudget

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddExpense : AppCompatActivity() {

    private lateinit var btnSelectDate: Button
    private lateinit var btnStartTime: Button
    private lateinit var btnEndTime: Button
    private lateinit var etDescription: EditText
    private lateinit var etAmount: EditText
    private lateinit var etCategory: AutoCompleteTextView
    private lateinit var btnTakePhoto: Button
    private lateinit var ivReceipt: ImageView
    private lateinit var btnSaveExpense: Button

    private var selectedDate = ""
    private var startTime = ""
    private var endTime = ""
    private var currentPhotoPath = ""
    private var photoUri: Uri? = null

    //permission launcher for the camera
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission is needed to take photos", Toast.LENGTH_LONG).show()
        }
    }

    //launching camera
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            photoUri?.let { uri ->
                ivReceipt.setImageURI(uri)
                ivReceipt.visibility = ImageView.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val selectedCategory = intent.getStringExtra("SELECTED_CATEGORY") ?: ""
        val extractedAmount = intent.getDoubleExtra("EXTRACTED_AMOUNT", -1.0)
        val extractedDate = intent.getStringExtra("EXTRACTED_DATE")
        val extractedDescription = intent.getStringExtra("EXTRACTED_DESCRIPTION")
        val extractedPhotoPath = intent.getStringExtra("EXTRACTED_PHOTO_PATH")

        if (extractedAmount > 0) {
            etAmount.setText(extractedAmount.toString())
            Toast.makeText(this, "Amount auto-filled from receipt!", Toast.LENGTH_SHORT).show()
        }

        if (!extractedDate.isNullOrEmpty()) {
            selectedDate = extractedDate
            btnSelectDate.text = selectedDate
        }

        if (!extractedDescription.isNullOrEmpty()) {
            etDescription.setText(extractedDescription)
        }

        if (!extractedPhotoPath.isNullOrEmpty()) {
            currentPhotoPath = extractedPhotoPath
            val photoFile = File(currentPhotoPath)
            if (photoFile.exists()) {
                ivReceipt.setImageURI(Uri.fromFile(photoFile))
                ivReceipt.visibility = ImageView.VISIBLE
            }
        }

        initializeViews()
        setupDatePicker()
        setupTimePickers()
        setupCamera()
        setupCategoryDropdown()

        //prefill category if coming from category list click
        if (selectedCategory.isNotEmpty()) {
            etCategory.setText(selectedCategory, false)
        }

        //back button
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        btnSaveExpense.setOnClickListener { saveExpense() }
    }

    private fun initializeViews() {
        btnSelectDate = findViewById(R.id.btnSelectDate)
        btnStartTime = findViewById(R.id.btnStartTime)
        btnEndTime = findViewById(R.id.btnEndTime)
        etDescription = findViewById(R.id.etDescription)
        etAmount = findViewById(R.id.etAmount)
        etCategory = findViewById(R.id.etCategory)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        ivReceipt = findViewById(R.id.ivReceipt)
        btnSaveExpense = findViewById(R.id.btnSaveExpense)
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, AppData.categoryList)
        etCategory.setAdapter(adapter)
        etCategory.threshold = 1
    }

    private fun setupDatePicker() {
        btnSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    btnSelectDate.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    private fun setupTimePickers() {
        btnStartTime.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Select Start Time")
                .build()
            picker.addOnPositiveButtonClickListener {
                startTime = String.format(Locale.US, "%02d:%02d", picker.hour, picker.minute)
                btnStartTime.text = startTime
            }
            picker.show(supportFragmentManager, "start_time")
        }

        btnEndTime.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Select End Time")
                .build()
            picker.addOnPositiveButtonClickListener {
                endTime = String.format(Locale.US, "%02d:%02d", picker.hour, picker.minute)
                btnEndTime.text = endTime
            }
            picker.show(supportFragmentManager, "end_time")
        }
    }

    private fun setupCamera() {
        btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndOpen()
        }
    }

    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val fileName = "JPEG_${timeStamp}_"
            val storageDir = getExternalFilesDir(null)
            val photoFile = File.createTempFile(fileName, ".jpg", storageDir)
            currentPhotoPath = photoFile.absolutePath

            photoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

            //granting permission to the camera app
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            if (intent.resolveActivity(packageManager) != null) {
                takePictureLauncher.launch(intent)
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error opening camera: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveExpense() {
        val description = etDescription.text.toString().trim()
        val amountText = etAmount.text.toString().trim()
        val category = etCategory.text.toString().trim()

        if (description.isEmpty() || amountText.isEmpty() || category.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        //conversion of string date to long for the rooom db
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateLong = try {
            sdf.parse(selectedDate)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }

        val expense = ExpenseEntity(
            userId = AppData.currentUser,
            date = dateLong,
            dateString = selectedDate,
            startTime = startTime.ifEmpty { "00:00" },
            endTime = endTime.ifEmpty { "00:00" },
            description = description,
            category = category,
            amount = amount,
            photoPath = currentPhotoPath
        )

        AppData.addExpense(expense, this)

        val gamificationManager = GamificationManager(this)
        gamificationManager.recordExpenseEntry(hasPhoto = currentPhotoPath.isNotEmpty())

        Toast.makeText(this, "Expense saved successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
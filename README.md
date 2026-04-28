
# TriBudgetApp

## YouTube Demonstration
[Watch the Demo Video](https://youtu.be/eTahLhGg-xU)

---

# Overview

TriBudgetApp is a mobile budgeting application designed to help users manage their personal finances effectively. The application allows users to track expenses, monitor budgets, analyse spending habits, and improve financial discipline through gamification features.

The application supports offline functionality using local storage and includes advanced tools such as receipt scanning using OCR and predictive spending insights.

---

# Features and Functionality

## 1. User Access
- Users create an account using a username and password
- Each user has their own personal budgeting data
- Secure login functionality allows users to access their financial records

---

## 2. Category System
Users can create expense categories such as:
- Food
- Transport
- Rent
- Entertainment

All expenses are grouped according to their selected category.

---

## 3. Expense Recording
Users can add expense entries containing:
- Expense amount
- Date
- Description
- Selected category
- Optional receipt/photo attachment

Attached receipts can later be viewed from the expense history.

---

## 4. Budget Control
Users are able to:
- Set a monthly spending limit (maximum budget)
- Set an optional minimum spending goal
- Track spending progress against these limits

The application continuously monitors spending behaviour and provides visual feedback.

---

## 5. Viewing Expenses
Users can:
- View all recorded expenses
- Filter expenses by date range
- Open and view attached receipt images
- Monitor historical spending activity

---

## 6. Spending Analysis
The application provides analytical tools including:
- Total spending per category
- Date-range filtering
- Spending trend visualisation
- Graphical analysis using charts

Charts are implemented using MPAndroidChart.

---

## 7. Dashboard Overview
The dashboard provides users with:
- Progress bars
- Budget summaries
- Spending indicators

### Budget Colour Indicators
- Green = Within budget
- Amber = Warning level
- Red = Overspending

---

## 8. Gamification Features
To improve user engagement and financial discipline, the application includes gamification features.

### Users can earn badges for:
- Logging expenses daily
- Maintaining spending streaks
- Staying under budget
- Uploading receipts consistently

### Level System
Users level up based on:
- Consistency
- Spending discipline
- App activity

---

## 9. Advanced Features

### OCR Receipt Scanning
- Google ML Kit OCR is used to scan receipts
- Expense information can be extracted automatically

### Predictive Insights
- The app forecasts future spending patterns
- Helps users anticipate overspending

### Collaborative Budgeting
- Shared budgeting allows multiple users to manage a budget together

### Dark Mode
- Supports both manual and automatic dark mode switching

---

## 10. Data Storage
- All data is stored locally on the device
- Uses Room Database / SQLite
- Application supports offline functionality
- Data remains permanently saved unless deleted by the user

---

# Technologies Used
- Kotlin
- Android Studio
- Room Database
- SQLite
- Google ML Kit
- CameraX
- MPAndroidChart
- Coroutines
- Gson

---

# Reference List

1. Android Developers (2024) *Pickers: DatePicker and TimePicker*. Available at: https://developer.android.com/develop/ui/views/components/pickers (Accessed: 28 April 2026).

2. Android Developers (2024) *Save data in a local database using Room*. Available at: https://developer.android.com/training/data-storage/room (Accessed: 28 April 2026).

3. Android Developers (2024) *View layout: RelativeLayout*. Available at: https://developer.android.com/develop/ui/views/layout/relative (Accessed: 28 April 2026).

4. Buddy, R.A. and B., M. (2025) *receipt_recognition 0.1.8*. Available at: https://pub.dev/packages/receipt_recognition (Accessed: 15 April 2026).

5. Divay, W. (2024) *Gamifying financial literacy: a marketing strategy for a mobile app using gamification to improve user engagement*. Master’s thesis. Universidade Católica Portuguesa. Available at: https://repositorio.ucp.pt/entities/publication/8c07a241-6871-4d33-a81b-bb39e3257923 (Accessed: 25 April 2026).

6. Dot Dot Fire (2024) *Money Wise Game has now been played by over half a million people*. MCV/Develop. Available at: https://mcvuk.com/business-news/from-the-industry-dot-dot-fires-money-wise-game-has-now-been-played-by-over-half-a-million-people-and-the-studio-is-expanding-for-its-follow-up/ (Accessed: 20 April 2026).

7. Google (2024) *androidx.camera.core*. Android Developers. Available at: https://developer.android.com/reference/androidx/camera/core/package-summary.html (Accessed: 17 April 2026).

8. Google (2025a) *androidx.camera.featurecombinationquery 1.5.2*. Android Developers. Available at: https://developer.android.com/jetpack/androidx/releases/camera-featurecombinationquery (Accessed: 17 April 2026).

9. Google (2025b) *androidx.camera.media3 1.0.0-alpha04*. Android Developers. Available at: https://developer.android.google.cn/jetpack/androidx/releases/camera-media3 (Accessed: 17 April 2026).

10. Google Codelabs (2023) *Android Room with a View - Kotlin*. Available at: https://developer.android.com/codelabs/android-room-with-a-view-kotlin (Accessed: 28 April 2026).

11. Google Developers (2024) *Easily add document scanning capability to your app with ML Kit Document Scanner API*. Android Developers Blog. Available at: https://android-developers.googleblog.com/2024/02/ml-kit-document-scanner-api.html (Accessed: 25 April 2026).

12. Google Gson Team (2024) *google-gson: A Java library for JSON conversion*. Google Open Source. Available at: https://android.googlesource.com/platform/external/gson (Accessed: 25 April 2026).

13. Google ML Kit (2024) *ML Kit release notes*. Google for Developers. Available at: https://developers.google.cn/ml-kit/release-notes (Accessed: 25 April 2026).

14. Google ML Kit Team (2026a) *Migrating to ML Kit for Android*. Google for Developers. Available at: https://developers.google.com/ml-kit/migration/android (Accessed: 20 April 2026).

15. Google ML Kit Team (2026b) *Recognize text in images with ML Kit on Android*. Google for Developers. Available at: https://developers.google.com/ml-kit/vision/text-recognition/v2/android (Accessed: 20 April 2026).

16. Jahnavi, M. (2024a) *JitPack.io – MPAndroidChart repository*. JitPack. Available at: https://jitpack.io (Accessed: 20 April 2026).

17. Jahnavi, M. (2024b) *MPAndroidChart Repository*. Available at: https://github.com/PhilJay/MPAndroidChart (Accessed: 28 April 2026).

18. JetBrains (2024) *kotlinx.coroutines: Library support for Kotlin coroutines*. Google Open Source. Available at: https://android.googlesource.com/platform/external/kotlinx.coroutines (Accessed: 25 April 2026).

19. Kotlin Documentation (2024) *Kotlin DSL for Gradle*. Available at: https://docs.gradle.org/current/userguide/kotlin_dsl.html (Accessed: 28 April 2026).

20. maqa544 (2024) *Receipt Scanner App – Android application utilizing Google ML Kit for OCR to scan and extract items and item prices from receipts*. GitHub. Available at: https://github.com/maqa544/android-receipt-ocr (Accessed: 17 April 2026).

21. Shindd9908 (2023) *Flutter_OCR_Bill_Scanner: A Flutter application designed for OCR scanning of bills*. GitHub. Available at: https://github.com/Shindd9908/Flutter_OCR_Bill_Scanner (Accessed: 17 April 2026).

22. Spendee (2026) *Spendee – The only app that gets your money into shape*. Elite AI Tools. Available at: https://eliteai.tools/tool/spendee (Accessed: 25 April 2026).

23. Stack Overflow (2024) *How to display an image from a URI in Android*. Available at: https://stackoverflow.com/questions/38352148/how-to-get-image-from-uri (Accessed: 28 April 2026).

24. StriveCloud (2026) *5 ways Revolut creates the best banking app with gamification*. Available at: https://www.strivecloud.io/blog/gamification-examples-banking (Accessed: 25 April 2026).

---

# AI Declaration 1

I declare that the Artificial Intelligence (AI) tool Gemini was used as an aid in the development of the Tri-Budget application for the following purposes:

## Logic Resolution
AI was used to resolve various errors regarding:
- User-selectable date filtering
- Retrieval of stored photo URIs

## Build Configuration
AI was used to resolve errors within the build.gradle.kts (app) file related to:
- Dependency versioning
- Plugin conflicts

## Structural Integrity
AI was used to resolve:
- ID mismatches between Kotlin files and XML layouts
- Context-related errors

## Code Maintenance
AI was used to assist with:
- Code indentation
- Code structure
- Technical comments

## UI Styling
AI was used to resolve styling and alignment issues using RelativeLayout to ensure a professional and consistent header format.

All AI-assisted fixes were reviewed, tested, and understood before integration. The final application represents original work with AI serving as a support tool for debugging and error resolution.

### Student Information
- Name: Tinodiwanashe Dzoro
- Student ID: ST10402234
- Date: 28 April 2026

## AI Reference
Google (2026) *Gemini AI Assistant*. Google. Available at: https://gemini.google.com (Accessed: 28 April 2026).

---

# AI Declaration 2

I declare that Artificial Intelligence (AI) tools ChatGPT and DeepSeek were used as assistance tools in the development of the Tri-Budget application for the following purposes:

## Camera and Photo Functionality
Implementation assistance for:
- CameraX
- FileProvider configuration
- Runtime permission handling
- AddExpense.kt
- ReceiptScannerActivity.kt

## OCR Receipt Scanner
Assistance with:
- Google ML Kit Text Recognition integration
- Regex pattern development for data extraction
- Confidence scoring implementation
- OCRProcessor.kt

## Code Error Resolution
AI assistance was used for:
- Null safety fixes
- Import resolution
- Type mismatch fixes
- Locale warnings
- KTX extension implementation

All AI-generated code was reviewed, tested, and understood before integration. The final application represents original work with AI serving as a development assistance tool.

### Student Information
- Name: Tapiwa Sango
- Student ID: ST10276025
- Date: 25 April 2026

## AI References

DeepSeek (2026) *DeepSeek AI Assistant*. DeepSeek. Available at: https://chat.deepseek.com (Accessed: 25 April 2026).

OpenAI (2025) *ChatGPT*. OpenAI. Available at: https://chat.openai.com (Accessed: 25 April 2026).
```

 

 

 

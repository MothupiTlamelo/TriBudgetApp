plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.tribudget"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.tribudget"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    // Gson for JSON parsing (saving expenses and categories)
    implementation("com.google.code.gson:gson:2.10.1")
    // CardView for the goals screen UI
    implementation("androidx.cardview:cardview:1.0.0")
    // Material Design components (TimePicker, etc.)
    // You might already have this, but ensure it's here
    implementation("com.google.android.material:material:1.11.0")
    // ML Kit for OCR (Receipt Scanner)
    implementation("com.google.mlkit:text-recognition:16.0.0")
    // MPAndroidChart for visual dashboard
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // Gson for shared budgeting sync
    implementation("com.google.code.gson:gson:2.10.1")
    // WorkManager for background predictive analytics
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    // CameraX for custom camera (better for scanning)
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    // For image cropping and rotation
    implementation("com.github.yalantis:ucrop:2.2.8")
    // Coroutines for async processing
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // ML Kit for OCR
    implementation("com.google.mlkit:text-recognition:16.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
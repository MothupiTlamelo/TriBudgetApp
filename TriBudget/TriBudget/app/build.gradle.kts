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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
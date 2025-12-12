plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //id("kotlin-kapt")
    alias(libs.plugins.ksp)

    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.google.gms.google.services) // <-- AÑADIDO
}

android {
    namespace = "com.example.nutricheckproject"
    compileSdk = 36 // <-- MODIFICADO

    defaultConfig {
        applicationId = "com.example.nutricheckproject"
        minSdk = 28
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
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.mediation.test.suite)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Navigation Component (Type-Safe)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Lifecycle, ViewModel y LiveData/StateFlow
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx) // StateFlow

    // Room (Base de datos local)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Para Coroutines y Flow
    //kapt(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler.v261)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
}

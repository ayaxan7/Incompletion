import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}
val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties().apply {
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}
val gmapsApiKey = localProperties.getProperty("GMAPS_API_KEY") ?: "MISSING_API_KEY"
android {
    namespace = "com.ayaan.incompletion"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ayaan.incompletion"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "GMAPS_API_KEY", "\"$gmapsApiKey\"")
        manifestPlaceholders["MAPS_API_KEY"] = gmapsApiKey
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
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.maps)
    ksp(libs.google.hilt.compiler)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.androidx.biometric)
    implementation(libs.kotlin.reflect)
    implementation(libs.accompanist.systemuicontroller)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt Testing
    androidTestImplementation(libs.dagger.hilt.android.testing)
    kspAndroidTest(libs.google.hilt.compiler)
    testImplementation(libs.dagger.hilt.android.testing)
    kspTest(libs.google.hilt.compiler)
    implementation (libs.androidx.credentials)
    implementation( libs.androidx.credentials.play.services.auth)
    implementation (libs.googleid)
//    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
//    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
//    implementation("com.google.android.gms:play-services-base:18.7.2")
//    implementation("com.google.android.gms:play-services-auth:21.3.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.firebase.auth)
    ksp(libs.google.hilt.compiler)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.androidx.biometric)
    implementation(libs.kotlin.reflect)
    implementation(libs.accompanist.systemuicontroller)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt Testing
    androidTestImplementation(libs.dagger.hilt.android.testing)
    kspAndroidTest(libs.google.hilt.compiler)
    testImplementation(libs.dagger.hilt.android.testing)
    kspTest(libs.google.hilt.compiler)
    implementation (libs.androidx.credentials)
    implementation( libs.androidx.credentials.play.services.auth)
    implementation (libs.googleid)
    implementation(libs.maps.compose)
    implementation(platform("com.google.firebase:firebase-bom:34.2.0"))
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
    implementation("com.google.android.gms:play-services-base:18.7.2")
    implementation("com.google.android.gms:play-services-auth:21.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
    implementation("com.google.android.libraries.places:places:4.4.1")
}
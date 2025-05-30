plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.projetandroiddorspasteau"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projetandroiddorspasteau"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.core.ktx.v1160) // Ou version plus récente
    implementation(libs.androidx.appcompat.v161) // Pour AppCompatActivity, Toolbar
    implementation(libs.material.v1110) // Pour Material Components
    implementation(libs.androidx.constraintlayout.v214) // Utile pour les layouts complexes
// Retrofit & Gson Converter
    implementation(libs.retrofit) // Vérifie la dernière version
    implementation(libs.converter.gson)

    // Glide (pour le chargement d'images)
    implementation(libs.glide) // Vérifie la dernière version
    kapt("com.github.bumptech.glide:compiler:4.16.0") // Pour Kotlin

    // RecyclerView (fait partie d'AndroidX, devrait déjà être là via appcompat ou material, mais pour être explicite)
    implementation(libs.androidx.recyclerview)

    // CardView (pour l'effet de vignette)
    implementation(libs.androidx.cardview)

    // Coroutines (pour les appels réseau asynchrones)
    implementation(libs.kotlinx.coroutines.core) // Vérifie la dernière version
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.android.gif.drawable)

    implementation(libs.androidx.datastore.preferences)
}
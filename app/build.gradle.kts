plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.lily.lilyiumplayer"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.lily.lilyiumplayer"
        minSdk = 30
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
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.compose.ui.unit)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.ui.text)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.android.material:material:1.12.0")

    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")
    
    implementation("androidx.datastore:datastore-preferences:1.2.0")

    val nav_version = "2.9.7"
    implementation("androidx.navigation:navigation-compose:$nav_version")


    val moshiVersion = "1.15.2"
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    implementation("com.squareup.moshi:moshi-adapters:$moshiVersion")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    implementation("com.squareup.picasso:picasso:2.8")

//    =================================================================

    val room_version = "2.8.3"
// Room runtime
    implementation("androidx.room:room-runtime:$room_version")

// Room Kotlin extensions (Coroutines & Flow support)
    implementation("androidx.room:room-ktx:$room_version")

// Room compiler (KSP for Kotlin)
    ksp("androidx.room:room-compiler:$room_version")

// Optional: Room Testing (for unit tests)
    testImplementation("androidx.room:room-testing:$room_version")


// Coil
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")


//    Icons
    implementation("androidx.compose.material:material-icons-extended")

// Media3
    val media3_version = "1.9.2"

    implementation("androidx.media3:media3-exoplayer:$media3_version")
    implementation("androidx.media3:media3-common:$media3_version")
    implementation("androidx.media3:media3-exoplayer:1.9.2")
    implementation("androidx.media3:media3-session:1.9.2")
    implementation("androidx.media3:media3-common:1.9.2")

//https://github.com/eygraber/compose-placeholder
    implementation("com.eygraber:compose-placeholder-material3:1.0.12")

//    https://github.com/mahozad/wavy-slider
    implementation("ir.mahozad.multiplatform:wavy-slider:2.2.0")

    implementation("androidx.paging:paging-runtime:3.3.6")
    implementation("androidx.paging:paging-compose:3.3.6")
}


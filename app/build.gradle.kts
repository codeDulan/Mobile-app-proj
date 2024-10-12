plugins {
    alias(libs.plugins.android.application)

    // IM/2021/025 - Google Services
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.dishdash"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.dishdash"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // IM/2021/025 - Enable view binding
    viewBinding {
        enable = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Dependency to add GIF support in the app
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.29")

    // IM/2021/025 - Firebase (Firebase BOM to manage versions)
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation(libs.firebase.auth)
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")

    implementation ("com.google.android.gms:play-services-auth:20.5.0")
    implementation ("com.google.firebase:firebase-auth:21.0.1")

    implementation ("com.facebook.android:facebook-android-sdk:latest.release")

    //glide
    implementation("com.github.bumptech.glide:glide:4.15.1")

    //image picker
    implementation("com.github.jrvansuita:PickImage:+")

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Kotlin script runtime
    implementation(kotlin("script-runtime"))

    // IM/2021/001 - Dependencies for ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")




}

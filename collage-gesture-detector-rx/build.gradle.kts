plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.cardinalblue.gesture.rx"

    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
    }

    compileOptions {
        sourceCompatibility = Versions.compatibilityJava
        targetCompatibility = Versions.compatibilityJava
    }

    kotlinOptions {
        jvmTarget = Versions.kotlinJvmTarget
    }
}

dependencies {
    // Kotlin
    implementation(libs.jetBrains.stdlib)

    // RxJava
    implementation(libs.bundles.rxjava.core)

    cbModules {
        // reference back to lib from PicCollage setup
        + libCollageGestureDetector
    }
}
plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.cardinalblue.demo"
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = 1
        versionName  = "1.0"
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
    // Google Support Library.
    implementation(libs.jetpack.appcompat)
    implementation(libs.jetpack.vectorDrawable)
    implementation(libs.jetpack.recyclerview)
    implementation(libs.jetpack.constraintlayout)

    // Multi-dex.
    implementation(libs.jetpack.multidex)

    // Kotlin
    implementation(libs.jetBrains.stdlib)

    // RxJava
    implementation(libs.bundles.rxjava.core)

    // My Libraries.
    cbModules {
        // reference back to lib from PicCollage setup
        + libCollageGestureDetector
        + libCollageGestureDetectorRx
    }

    // Unit Test
    implementation(libs.test.junit)

    // Instrumental Test
    implementation(libs.test.runner)
    implementation(libs.test.espressoCore)
}
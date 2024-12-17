plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.cardinalblue.gesture"

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

    // Unit tests.
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockito)
    testImplementation(libs.test.robolectric)

    // Instrumentation tests.
    testImplementation(libs.test.runner)
    testImplementation(libs.test.espressoCore)
}
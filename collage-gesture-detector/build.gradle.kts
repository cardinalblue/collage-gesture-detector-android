import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    id("kotlin-android")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(Versions.kotlinJvmTarget))
    }
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
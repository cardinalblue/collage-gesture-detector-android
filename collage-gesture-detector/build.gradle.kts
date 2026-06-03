plugins {
    id("com.android.library")
}

android {
    namespace = "com.cardinalblue.gesture"

    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
    }

    compileOptions {
        sourceCompatibility = Versions.compatibilityJava
        targetCompatibility = Versions.compatibilityJava
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
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
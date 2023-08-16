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
    implementation(Dependencies.Kotlin.stdlib)

    // Unit tests.
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.mockito)
    testImplementation(Dependencies.Test.robolectric)

    // Instrumentation tests.
    testImplementation(Dependencies.Test.runner)
    testImplementation(Dependencies.Test.espressoCore)
}
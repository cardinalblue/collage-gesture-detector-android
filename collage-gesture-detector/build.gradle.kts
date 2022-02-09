plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
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
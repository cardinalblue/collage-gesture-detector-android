plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(Versions.compileSdk)
    buildToolsVersion(Versions.buildTool)

    defaultConfig {
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.targetSdk)
        versionCode = 1
        versionName  = "1.0"
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
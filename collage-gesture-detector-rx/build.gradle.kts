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
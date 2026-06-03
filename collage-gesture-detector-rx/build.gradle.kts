plugins {
    id("com.android.library")
}

android {
    namespace = "com.cardinalblue.gesture.rx"

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

    // RxJava
    implementation(libs.bundles.rxjava.core)

    cbModules {
        // reference back to lib from PicCollage setup
        + libCollageGestureDetector
    }
}
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

    // RxJava
    implementation(Dependencies.RxJava.rxJava)
    implementation(Dependencies.RxJava.rxAndroid)
    implementation(Dependencies.RxJava.rxKotlin)

    cbModules {
        // reference back to lib from PicCollage setup
        libCollageGestureDetector()
    }
}
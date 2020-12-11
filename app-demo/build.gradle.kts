plugins {
    id("com.android.application")
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

    compileOptions {
        sourceCompatibility = Versions.compatibilityJava
        targetCompatibility = Versions.compatibilityJava
    }
}

dependencies {
    // Google Support Library.
    implementation(Dependencies.JetPack.appcompat)
    implementation(Dependencies.JetPack.vectorDrawable)
    implementation(Dependencies.JetPack.recyclerView)
    implementation(Dependencies.JetPack.constraintLayout)

    // Multi-dex.
    implementation(Dependencies.JetPack.multidex)

    // Kotlin
    implementation(Dependencies.Kotlin.stdlib)

    // RxJava
    implementation(Dependencies.RxJava.rxJava)
    implementation(Dependencies.RxJava.rxAndroid)
    implementation(Dependencies.RxJava.rxBinding)
    implementation(Dependencies.RxJava.rxKotlin)

    // My Libraries.
    cbModules {
        // reference back to lib from PicCollage setup
        libCollageGestureDetector()
        libCollageGestureDetectorRx()
    }

    // Unit Test
    implementation(Dependencies.Test.junit)

    // Instrumental Test
    implementation(Dependencies.Test.runner)
    implementation(Dependencies.Test.espressoCore)
}
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.cool.music"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cool.music"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {



    implementation ("com.alibaba:fastjson:1.2.83")
    implementation ("org.greenrobot:eventbus:3.2.0")
    //implementation("com.github.afollestad:material-dialogs-core:3.3.0-alpha1")
    implementation("com.google.android.exoplayer:exoplayer:2.18.1")
    implementation("com.mpatric:mp3agic:0.9.1")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ✨ PhotoView 库（支持缩放、拖拽）
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
}
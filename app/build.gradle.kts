plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.devsyncit.instagramclone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.devsyncit.instagramclone"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.firebase.messaging.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.squareup.retrofit2:retrofit:3.0.0")
    implementation ("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-auth")

    //agora
    implementation("io.agora.rtc:full-sdk:4.5.0")

    implementation ("androidx.lifecycle:lifecycle-process:2.6.2")

    //glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
}
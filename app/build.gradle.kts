plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.lunapic"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lunapic"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField(
            "String",
            "AWS_ENDPOINT",
            "\"http://10.0.2.2:4566\""
        )
        buildConfigField(
            "String",
            "AWS_ACCESS_KEY",
            "\"ABCDEFGHIJKLMNOPQRST\""
        )
        buildConfigField(
            "String",
            "AWS_SECRET_ACCESS_KEY",
            "\"1234567890123456789012345678901234567890\""
        )
        buildConfigField(type = "boolean", "FORCED_PATH_STYLE", value = "true")
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            buildConfigField("boolean", "COIL_DEBUGGER", "true")
        }

        create("local") {
            dimension = "environment"
            applicationIdSuffix = ".local"
            buildConfigField(type = "boolean", "COIL_DEBUGGER", "false")
        }

        create("production") {
            dimension = "environment"
            buildConfigField(
                "String",
                "AWS_ENDPOINT",
                "\"https://s3.sa-east-1.amazonaws.com\""
            )
            buildConfigField(
                "String",
                "AWS_ACCESS_KEY",
                project.findProperty("AWS_ACCESS_KEY").toString()
            )
            buildConfigField(
                "String",
                "AWS_SECRET_ACCESS_KEY",
                project.findProperty("AWS_SECRET_ACCESS_KEY").toString()
            )
            buildConfigField("boolean", "COIL_DEBUGGER", "false")
            buildConfigField(type = "boolean", "FORCED_PATH_STYLE", value = "false")
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // coil
    implementation(libs.coil.compose)
    implementation(libs.coil)
    implementation(libs.coil.gif)

    // kotlin serialization
    implementation(libs.kotlinx.serialization.json)

    // nav-compose
    implementation(libs.androidx.navigation.compose)

    // s3
    implementation(libs.s3)

    // hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // room
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
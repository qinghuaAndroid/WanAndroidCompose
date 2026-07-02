plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.android.room3)
    alias(libs.plugins.kotlin.parcelize)
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.wan.android.library"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // define a BOM and its version
    implementation(platform(libs.okhttp.bom))
    // define any required OkHttp artifacts without version
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // ** jetpack liftcycle **
    api(libs.androidx.lifecycle.viewmodel.ktx)
    // ViewModel utilities for Compose
    api(libs.androidx.lifecycle.viewmodel.compose)
    // LiveData
    api(libs.androidx.lifecycle.livedata.ktx)
    // Lifecycles only (without ViewModel or LiveData)
    api(libs.androidx.lifecycle.runtime.ktx)
    // Lifecycle utilities for Compose
    api(libs.androidx.lifecycle.runtime.compose)
    // Saved state module for ViewModel
    api(libs.androidx.lifecycle.viewmodel.savedstate)
    // ViewModel integration with Navigation3
    api(libs.androidx.lifecycle.viewmodel.navigation3)
    // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
    api(libs.androidx.lifecycle.process)
    // Annotation processor
    ksp(libs.androidx.lifecycle.compiler)

    // ** jetpack navigation3 **
    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)

    // ** jetpack hilt **
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    //jetpack - room3
    implementation(libs.androidx.room3.runtime)
    ksp(libs.androidx.room3.compiler)

    // splitties
    api(libs.splitties.appctx)

    //MMKV(tencent出品，类似sp)
    api(libs.mmkv)
}
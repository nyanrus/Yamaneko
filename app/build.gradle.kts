plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "dev.nyanrus.yamaneko"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.nyanrus.yamaneko"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

val mozComponentVersion = "123.0"
//val mozComponentVersion = "125.0.20240225160348"

//val geckoviewChannel = "arm64-v8a"
//val geckoviewVersion = "123.0.20240213221259"

configurations.all {
    resolutionStrategy.capabilitiesResolution.withCapability("org.mozilla.telemetry:glean-native") {
        selectHighestVersion()
    }
}

dependencies {
    //implementation("org.mozilla.geckoview:geckoview-$geckoviewChannel:$geckoviewVersion")

    implementation("org.mozilla.components:concept-engine:$mozComponentVersion")

    implementation("org.mozilla.components:compose-engine:$mozComponentVersion")

    implementation("org.mozilla.components:browser-engine-gecko:$mozComponentVersion")
    implementation("org.mozilla.components:browser-state:$mozComponentVersion")

    for (s in listOf(
        "feature-awesomebar",
        "feature-fxsuggest",
        "feature-search",
        "feature-session",
        "feature-tabs",

        "service-location",
        //"support-rusthttp"
    )) {
        implementation("org.mozilla.components:$s:$mozComponentVersion")
    }

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))

    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:3.0-alpha-1")
}
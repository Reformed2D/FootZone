plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") version "4.4.0"

}

android {
    namespace = "com.example.versionfinal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.versionfinal"
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

    packaging {
        resources {
            excludes.add("META-INF/NOTICE.md")
            excludes.add("META-INF/LICENSE.md")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Material Design
    implementation("com.google.android.material:material:1.9.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
<<<<<<< Updated upstream
    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation ("com.sun.mail:android-activation:1.6.7")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.zxing:core:3.4.1")

=======
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
>>>>>>> Stashed changes
    // AndroidX et autres dépendances
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
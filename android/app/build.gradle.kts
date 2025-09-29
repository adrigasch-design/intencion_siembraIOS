import java.util.Properties
import java.io.FileInputStream

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

plugins {
    id("com.android.application")
    kotlin("android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

// Fuerza el toolchain de Kotlin a 17
kotlin {
    jvmToolchain(17)
}

// Lee versionCode / versionName desde local.properties o usa valores por defecto
val flutterVersionCode = project.findProperty("flutter.versionCode")?.toString() ?: "1"
val flutterVersionName = project.findProperty("flutter.versionName")?.toString() ?: "1.0"

android {
    namespace = "com.example.intencion_siembra"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = "27.0.12077973"

     compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // (opcional) si necesitas desugar APIs de Java 8+:
        // isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    
    defaultConfig {
        applicationId = "com.example.intencion_siembra"
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutterVersionCode.toInt()
        versionName = flutterVersionName
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                val storeFilePath = keystoreProperties.getProperty("storeFile")
                val storePasswordProp = keystoreProperties.getProperty("storePassword")
                val keyAliasProp = keystoreProperties.getProperty("keyAlias")
                val keyPasswordProp = keystoreProperties.getProperty("keyPassword")

                if (storeFilePath.isNullOrBlank() ||
                    storePasswordProp.isNullOrBlank() ||
                    keyAliasProp.isNullOrBlank() ||
                    keyPasswordProp.isNullOrBlank()
                ) {
                    throw GradleException(
                        "Faltan claves en key.properties. Revisa storeFile, storePassword, keyAlias y keyPassword."
                    )
                }

                storeFile = file(storeFilePath)
                storePassword = storePasswordProp
                keyAlias = keyAliasProp
                keyPassword = keyPasswordProp
                enableV1Signing = true
                enableV2Signing = true
            } else {
                throw GradleException("No se encontró android/key.properties. Créalo antes de compilar release.")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

flutter {
    source = "../.."
}

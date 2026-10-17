import java.net.URI

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jlleitschuh.gradle.ktlint")
}

val appVersion = file("${rootProject.projectDir}/version.txt").readText().trim().toInt()

android {
    namespace = "com.lumakernel.dimore"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.lumakernel.dimore"
        minSdk = 36
        targetSdk = 36
        versionCode = appVersion
        versionName = "v$appVersion.0.0"

        val repoUrl =
            System.getenv("GITHUB_REPOSITORY")?.let { "https://github.com/$it" }
                ?: try {
                    val process =
                        ProcessBuilder("git", "remote", "get-url", "origin")
                            .directory(rootProject.projectDir)
                            .redirectErrorStream(true)
                            .start()
                    val raw = process.inputStream.bufferedReader().readText().trim()
                    val parsed = URI(raw.replace(Regex("^([^@]+)@([^:]+):"), "$1://$2/"))
                    URI(
                        "https",
                        parsed.host,
                        parsed.path.removeSuffix(".git"),
                        null,
                    ).toString()
                } catch (_: Exception) {
                    ""
                }
        buildConfigField("String", "REPOSITORY_URL", "\"$repoUrl\"")
    }

    signingConfigs {
        val keystoreFile = System.getenv("KEYSTORE_FILE")
        if (keystoreFile != null) {
            create("release") {
                storeFile = file(keystoreFile)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfigs.findByName("release")?.let {
                signingConfig = it
            }
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
        buildConfig = true
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.15.0")

    // Shizuku API (optional - for overlay permission auto-grant)
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")
}

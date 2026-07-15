import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    )

    androidLibrary {
        namespace = "com.jecsdev.papipuntos.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }

        // Runs commonTest on the JVM host (works on Windows, unlike the iOS simulator tests).
        withHostTest {}
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.kotlincrypto.hmac.sha2)
            implementation(libs.kotlincrypto.crypto.rand)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// Room's KSP processor must run for every target that compiles the @Database.
dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

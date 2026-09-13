import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

// Supabase URL + anon key come from a gitignored secrets.properties at the project root,
// so the real values never reach version control. A missing file yields blank values: the
// app still compiles, remote sign-in just won't connect until secrets.properties is filled.
// A typed task (not an ad-hoc doLast) keeps this configuration-cache compatible; the file is
// an input (as a tolerant FileCollection) so editing the keys — or adding the file — re-runs it.
abstract class GenerateSupabaseConfig : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val secretsFiles: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val file = secretsFiles.files.firstOrNull()
        val secrets = Properties().apply {
            if (file != null && file.exists()) file.inputStream().use { load(it) }
        }
        val url = secrets.getProperty("supabase.url", "")
        val anonKey = secrets.getProperty("supabase.anonKey", "")
        val pkgDir = outputDir.get().asFile.resolve("com/jecsdev/papipuntos/data/remote")
        pkgDir.mkdirs()
        pkgDir.resolve("SupabaseConfig.kt").writeText(
            """
            |package com.jecsdev.papipuntos.data.remote
            |
            |// GENERATED from secrets.properties — do not edit by hand.
            |internal object SupabaseConfig {
            |    const val URL = "$url"
            |    const val ANON_KEY = "$anonKey"
            |}
            |""".trimMargin(),
        )
    }
}

val generateSupabaseConfig by tasks.registering(GenerateSupabaseConfig::class) {
    secretsFiles.from(rootProject.file("secrets.properties"))
    outputDir.set(layout.buildDirectory.dir("generated/supabaseConfig/kotlin"))
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
        commonMain {
            // The generated SupabaseConfig (URL + anon key) from the gitignored secrets.properties.
            kotlin.srcDir(generateSupabaseConfig)

            dependencies {
            implementation(projects.core.model)
            implementation(projects.core.domain)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.koin.core)

                implementation(libs.room.runtime)
                implementation(libs.sqlite.bundled)
                implementation(libs.kotlincrypto.hmac.sha2)
                implementation(libs.kotlincrypto.crypto.rand)

                // Supabase Auth (GoTrue) for remote/social sign-in. The Ktor HTTP engine is
                // provided per platform below (Supabase auto-selects the one on the classpath).
                implementation(libs.supabase.auth)
            }
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
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

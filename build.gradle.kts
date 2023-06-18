import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.compose") version "1.4.0"
    kotlin("plugin.serialization") version "1.6.10"
    idea
}

group = "com.x256n.regexapplier.desktop"
version = "1.0.2" // UPDATE version in Main.kt as well
description = ""

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://repo.repsy.io/mvn/chrynan/public")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.2")

    implementation("com.chrynan.navigation:navigation-core:0.4.0")
    implementation("com.chrynan.navigation:navigation-compose:0.4.0")

//    implementation("io.insert-koin:koin-core:3.2.0")
    implementation("io.insert-koin:koin-core-jvm:3.2.0")
//    implementation("org.postgresql:postgresql:42.3.6")

    implementation("ch.qos.logback:logback-classic:1.2.11")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
}

compose.desktop {
    application {
        mainClass = "com.x256n.regexapplier.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "regex-applier"
            packageVersion = project.version.toString()
            description = "UI application that applies a list of regexes to a text"
            copyright = "© 2023 liosha"
            licenseFile.set(project.file("LICENSE"))
            windows {
                dirChooser = true
                menuGroup = packageName
                shortcut = true
                menu = true
                iconFile.set(project.file("src/main/resources/icon.ico"))
            }
            linux {
                iconFile.set(project.file("src/main/resources/icon.png"))
                appCategory = "Utility"
                shortcut = true
                debMaintainer = "liosha"
                debPackageVersion = project.version.toString()
                rpmLicenseType = "MIT"
                rpmPackageVersion = project.version.toString()
            }
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
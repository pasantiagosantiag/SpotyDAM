import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.1.10"
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
kotlin {
    jvm("desktop") {
        //para poder usar código Java en el escritorio
        compilations.all {

        }
        withJava()
    }
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            //mongo
            //https://www.mongodb.com/docs/drivers/kotlin/coroutine/current/quick-start/

            runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
            implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.3.0")
            //mongo bson
            implementation("org.mongodb:bson-kotlinx:5.3.0")

            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha11")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.5.0")

            //adaptativo
            implementation("org.jetbrains.compose.material3.adaptive:adaptive:1.0.0-alpha03")
            implementation("org.jetbrains.compose.material3.adaptive:adaptive-layout:1.0.0-alpha03")
            implementation("org.jetbrains.compose.material3.adaptive:adaptive-navigation:1.0.0-alpha03")
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation("org.jetbrains.compose.material3:material3-window-size-class:1.7.3")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

            //gráficos
            implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:4.9.3")
            implementation("org.jetbrains.lets-plot:lets-plot-common:4.5.2")
            implementation("org.jetbrains.lets-plot:platf-awt:4.5.2")
            implementation("org.jetbrains.lets-plot:lets-plot-compose:2.1.1")


            //logs
            implementation("org.slf4j:slf4j-api:2.0.9") // SLF4J API
            implementation("ch.qos.logback:logback-classic:1.4.11") // Implementación de Logback
            //koin
            //implementation("io.insert-koin:koin-core:4.0.0") // Koin Core para KMP

            //koin
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewModel)
            //selecciona fichero
            //https://github.com/vinceglb/FileKit
            implementation("io.github.vinceglb:filekit-core:0.8.8")

            // Enables FileKit with Composable utilities
            implementation("io.github.vinceglb:filekit-compose:0.8.8")
            //imagenes
            implementation("io.coil-kt.coil3:coil-compose:3.1.0")
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")

        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)


            //implementation("io.insert-koin:koin-core:4.0.1")

            runtimeOnly("io.insert-koin:koin-compose-jvm:4.0.1")

        }
    }
}


compose.desktop {
    application {
        mainClass = "ies.sequeros.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ies.sequeros"
            packageVersion = "1.0.0"
            windows {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/music.ico")) // Para Windows
            }

            linux {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/icon.png")) // Para Linux
            }
        }
    }
}

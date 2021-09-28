import org.jetbrains.kotlin.contracts.model.structure.UNKNOWN_COMPUTATION.type

plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"
    id("org.jetbrains.compose") version "1.0.0-alpha3"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

version = "1.0.0"

dependencies {
    implementation("io.ktor:ktor-client-core:1.6.3")
    implementation("io.ktor:ktor-client-cio:1.6.3")
    implementation("io.ktor:ktor-client-serialization:1.6.3")
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.22")
    implementation("com.sksamuel.scrimage:scrimage-filters:4.0.22")
    implementation("com.sksamuel.scrimage:scrimage-webp:4.0.22")
    implementation("com.sksamuel.scrimage:scrimage-formats-extra:4.0.22")
    implementation("net.lingala.zip4j:zip4j:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("com.lordcodes.turtle:turtle:0.5.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.21")
    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.materialIconsExtended)
}

tasks.test {
    useJUnit()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}
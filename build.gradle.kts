import org.jetbrains.kotlin.contracts.model.structure.UNKNOWN_COMPUTATION.type

plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"
}

repositories {
    mavenCentral()
    google()
}

version = "2.0.1"

dependencies {
    implementation("io.ktor:ktor-client-core:1.6.4")
    implementation("io.ktor:ktor-client-cio:1.6.4")
    implementation("io.ktor:ktor-client-serialization:1.6.4")
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.22")
    implementation("com.sksamuel.scrimage:scrimage-filters:4.0.22")
    implementation("com.sksamuel.scrimage:scrimage-webp:4.0.22")
    implementation("com.sksamuel.scrimage:scrimage-formats-extra:4.0.22")
    implementation("net.lingala.zip4j:zip4j:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("com.lordcodes.turtle:turtle:0.5.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.21")
}

tasks.test {
    useJUnit()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}



tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Gradle",
            "Implementation-Version" to archiveVersion
        )
    }
}
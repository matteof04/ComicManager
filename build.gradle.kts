
plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    google()
}

version = "3.0.1"

dependencies {
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-cio:1.6.7")
    implementation("io.ktor:ktor-client-serialization:1.6.7")
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.23")
    implementation("com.sksamuel.scrimage:scrimage-filters:4.0.23")
    implementation("com.sksamuel.scrimage:scrimage-webp:4.0.23")
    implementation("com.sksamuel.scrimage:scrimage-formats-extra:4.0.23")
    implementation("net.lingala.zip4j:zip4j:2.9.1")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
    implementation("com.lordcodes.turtle:turtle:0.6.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.21")
}

tasks.test {
    useJUnit()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes["Main-Class"] = "com.github.matteof04.comicmanager.MainKt"
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version
    }
    archiveBaseName.set(project.name+"-shadow")
    archiveVersion.set(project.version.toString())
    mergeServiceFiles()
}
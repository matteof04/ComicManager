import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.22")
    implementation("com.sksamuel.scrimage:scrimage-filters:4.0.22")
    implementation("com.sksamuel.scrimage:scrimage-webp:4.0.22")
    implementation("com.sksamuel.scrimage:scrimage-formats-extra:4.0.22")
    implementation("net.lingala.zip4j:zip4j:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("com.lordcodes.turtle:turtle:0.5.0")
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
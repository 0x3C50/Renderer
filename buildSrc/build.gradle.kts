plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.quiltmc.org/repository/release")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    fun pluginDep(id: String, version: String) = "${id}:${id}.gradle.plugin:${version}"

    compileOnly(kotlin("gradle-plugin", embeddedKotlinVersion))
//    runtimeOnly(kotlin("gradle-plugin", "2.0.21"))

    implementation(pluginDep("fabric-loom", "1.13-SNAPSHOT"))

    implementation("com.squareup:javapoet:1.13.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}
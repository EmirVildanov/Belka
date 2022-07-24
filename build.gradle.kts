import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.10"
    application
}

group = "me.emir"
version = "1.0-SNAPSHOT"

val versionTelegramBot = "6.0.7"
val versionGuava = "31.1-jre"
val versionSlf4j = "1.7.36"
val versionKotlinLogging = "2.1.23"
val versionSerialization = "1.3.2"
val versionMongodb = "4.6.1"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    // telegram bot api
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:$versionTelegramBot")

    // mongodb
    implementation("org.litote.kmongo:kmongo-serialization:$versionMongodb")
    // mongodb through coroutine
//    implementation("org.litote.kmongo:kmongo-coroutine-serialization:$versionMongodb)


    // dependency just to read from resources folder
    implementation("com.google.guava:guava:$versionGuava")

    // logging
    implementation("org.slf4j:slf4j-api:$versionSlf4j")
    implementation("org.slf4j:slf4j-simple:$versionSlf4j")
    implementation("io.github.microutils:kotlin-logging-jvm:$versionKotlinLogging")

    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$versionSerialization")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
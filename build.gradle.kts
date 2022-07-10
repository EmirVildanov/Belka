import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.emir"
version = "1.0-SNAPSHOT"

val versionTelegramBot = "6.0.7"
val versionGuava = "31.1-jre"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:$versionTelegramBot")

    // dependency just to read from resources folder
    implementation("com.google.guava:guava:$versionGuava")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
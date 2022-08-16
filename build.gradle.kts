import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.10"
    id("io.gitlab.arturbosch.detekt").version("1.21.0")
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
val versionKtor = "2.0.3"
val versionJodaTime = "2.10.14"
val versionLettuce = "6.2.0.RELEASE"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    // telegram bot api
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:$versionTelegramBot")

    // ktor for making http requests
    implementation("io.ktor:ktor-client-core:$versionKtor")
    implementation("io.ktor:ktor-client-cio:$versionKtor")
    implementation("io.ktor:ktor-client-content-negotiation:$versionKtor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$versionKtor")

    // kmongo async driver for MondoDB
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:$versionMongodb")

    // Redis driver
    implementation("io.lettuce:lettuce-core:$versionLettuce")

    // dependency just to read from resources folder
    implementation("com.google.guava:guava:$versionGuava")

    // logging
    implementation("org.slf4j:slf4j-api:$versionSlf4j")
    implementation("org.slf4j:slf4j-simple:$versionSlf4j")
    implementation("io.github.microutils:kotlin-logging-jvm:$versionKotlinLogging")

    // joda-time for working with time
    implementation("joda-time:joda-time:$versionJodaTime")
    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$versionSerialization")
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "1.8"
}
tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.util.*

plugins {
	`kotlin-dsl`
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://maven.fabricmc.net/")
	maven("https://maven.architectury.dev/")
	maven("https://maven.neoforged.net/releases")
	maven("https://maven.firstdarkdev.xyz/releases")
	maven("https://maven.wagyourtail.xyz/releases")
	gradlePluginPortal()
}

fun DependencyHandler.plugin(id: String, version: String) {
	this.implementation(group = id, name = "$id.gradle.plugin", version = version)
}

tasks.compileKotlin {
	compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_2_0)
}

val properties = Properties().apply {
	load(rootDir.parentFile.resolve("gradle.properties").inputStream())
}

operator fun String.invoke(): String = properties.getProperty(this) ?: error("Property $this is not defined")

dependencies {
	plugin(id = "xyz.wagyourtail.unimined", version = "unimined_version"())
	plugin(id = "com.github.johnrengelman.shadow", version = "shadow_version"())
	plugin(id = "io.github.pacifistmc.forgix", version = "forgix_version"())
}
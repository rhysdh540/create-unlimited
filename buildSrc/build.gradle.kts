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
	maven("https://maven.wagyourtail.xyz/snapshots")
	gradlePluginPortal()
}

fun DependencyHandler.plugin(id: String, version: String) {
	this.implementation(group = id, name = "$id.gradle.plugin", version = version)
}

tasks.compileKotlin {
	compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_2_0)
	kotlinOptions.jvmTarget = "17"
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

val gradleProperties = Properties().apply {
	load(rootDir.parentFile.resolve("gradle.properties").inputStream())
}

operator fun String.invoke(): String = gradleProperties.getProperty(this) ?: error("Property $this is not defined")

dependencies {
	implementation("org.ow2.asm:asm-tree:${"asm_version"()}")
	implementation(group = "org.jetbrains", name = "annotations")

	plugin(id = "xyz.wagyourtail.unimined", version = "unimined_version"())
	plugin(id = "com.github.johnrengelman.shadow", version = "shadow_version"())
	plugin(id = "xyz.wagyourtail.unimined.expect-platform", version = "expectplatform_version"())
}

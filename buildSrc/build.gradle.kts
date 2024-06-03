import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

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
	gradlePluginPortal()
}

fun DependencyHandler.plugin(id: String, version: String) {
	this.implementation(group = id, name = "$id.gradle.plugin", version = version)
}

tasks.compileKotlin {
	compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_2_0)
}

dependencies {
//	plugin("architectury-plugin", "3.4.155")
//	plugin("dev.architectury.loom", "1.5.391")
//	plugin("com.github.johnrengelman.shadow", "8.1.1")
//	plugin("io.github.pacifistmc.forgix", "1.2.9")

//	implementation("org.ow2.asm:asm:9.7")
//	implementation("org.ow2.asm:asm-analysis:9.7")
}
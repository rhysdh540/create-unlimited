import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.util.Properties

plugins {
	`kotlin-dsl`
	idea
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

idea.module.setDownloadSources(true)

fun DependencyHandler.plugin(id: String, version: String) =
	implementation(group = id, name = "$id.gradle.plugin", version = version)

tasks.compileKotlin {
	compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_2_0)
	kotlinOptions.jvmTarget = "17"
}

val gradleProperties = Properties().apply {
	load(rootDir.parentFile.resolve("gradle.properties").inputStream())
}

operator fun String.invoke(): String = gradleProperties.getProperty(this) ?: error("Property $this is not defined")

dependencies {
	implementation("org.ow2.asm:asm-tree:${"asm_version"()}")
	implementation("org.ow2.asm:asm-commons:${"asm_version"()}")
	implementation(group = "org.jetbrains", name = "annotations")
	implementation("com.guardsquare:proguard-base:${"proguard_version"()}")

	plugin(id = "xyz.wagyourtail.unimined", version = "unimined_version"())
	plugin(id = "org.jetbrains.gradle.plugin.idea-ext", version = "idea_ext_version"())
	plugin(id = "xyz.wagyourtail.unimined.expect-platform", version = "expectplatform_version"())
}

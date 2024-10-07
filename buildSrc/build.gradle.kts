import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.util.Properties

plugins {
	`kotlin-dsl`
	idea
}

// warning: do not move down, that breaks things
val gradleProperties by lazy {
	Properties().apply {
		load(rootDir.parentFile.resolve("gradle.properties").inputStream())
	}
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

idea.module.isDownloadSources = true

tasks.compileKotlin {
	compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_2_0)
	kotlinOptions.jvmTarget = "java_version"()
}

dependencies {
	implementation("org.ow2.asm:asm-tree:${"asm_version"()}")
	implementation("org.ow2.asm:asm-commons:${"asm_version"()}")
	implementation(group = "org.jetbrains", name = "annotations")
	implementation("com.guardsquare:proguard-base:${"proguard_version"()}")

	plugin(id = "xyz.wagyourtail.commons-gradle", version = "commons_gradle_version"())
	plugin(id = "xyz.wagyourtail.unimined", version = "unimined_version"())
	plugin(id = "org.jetbrains.gradle.plugin.idea-ext", version = "idea_ext_version"())
	plugin(id = "xyz.wagyourtail.unimined.expect-platform", version = "expectplatform_version"())
}

operator fun String.invoke() = gradleProperties.getProperty(this) ?: error("No property \"$this\"")

fun DependencyHandler.plugin(id: String, version: String) =
	implementation(group = id, name = "$id.gradle.plugin", version = version)

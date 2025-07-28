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
	gradlePluginPortal()
	maven("https://maven.fabricmc.net")
	maven("https://maven.neoforged.net/releases")
	maven("https://maven.wagyourtail.xyz/releases")
	maven("https://maven.wagyourtail.xyz/snapshots")
}

idea.module.isDownloadSources = true

kotlin {
	compilerOptions.languageVersion = KotlinVersion.KOTLIN_2_0
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

dependencies {
	implementation("org.ow2.asm:asm-tree:${"asm_version"()}")
	implementation("org.ow2.asm:asm-commons:${"asm_version"()}")
	implementation("io.github.prcraftmc:class-diff:1.0-SNAPSHOT")
	implementation(group = "org.jetbrains", name = "annotations")
	implementation("com.guardsquare:proguard-base:${"proguard_version"()}")

	plugin(id = "com.gradleup.shadow", version = "shadow_version"())
	plugin(id = "fabric-loom", version = "loom_version"())
	plugin(id = "net.neoforged.moddev", version = "mdg_version"())
	plugin(id = "org.jetbrains.gradle.plugin.idea-ext", version = "idea_ext_version"())
	plugin(id = "xyz.wagyourtail.unimined.expect-platform", version = "expectplatform_version"())
}

operator fun String.invoke() = gradleProperties.getProperty(this) ?: error("No property \"$this\"")

fun DependencyHandler.plugin(id: String, version: String) =
	implementation(group = id, name = "$id.gradle.plugin", version = version)

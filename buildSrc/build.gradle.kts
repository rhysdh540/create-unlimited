@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
	`kotlin-dsl`
}

repositories {
	gradlePluginPortal()
	mavenCentral()
	maven("https://maven.neoforged.net/releases") {
		content {
			includeGroupAndSubgroups("net.neoforged")
		}
	}
	maven("https://maven.fabricmc.net/") {
		content {
			includeGroupAndSubgroups("net.fabricmc")
			includeGroup("fabric-loom")
		}
	}

	maven("https://maven.kikugie.dev/releases") {
		content {
			includeGroupAndSubgroups("dev.kikugie")
		}
	}

	maven("https://maven.wagyourtail.xyz/snapshots") {
		content {
			includeGroupAndSubgroups("xyz.wagyourtail")
		}
	}

	maven("https://maven.wagyourtail.xyz/releases") {
		content {
			includeGroupAndSubgroups("xyz.wagyourtail")
		}
	}

	mavenLocal() // for class-diff, for now
}

kotlin {
	jvmToolchain(21)
	compilerOptions.languageVersion = KotlinVersion.KOTLIN_2_2
	compilerOptions.apiVersion = KotlinVersion.KOTLIN_2_2
	compilerOptions.freeCompilerArgs.addAll(
		"-Xcontext-parameters"
	)
}

dependencies {
	implementation("xyz.wagyourtail.manifold:xyz.wagyourtail.manifold.gradle.plugin:1.1.0-SNAPSHOT")
	implementation("net.neoforged:moddev-gradle:2.0.107")
	implementation("net.fabricmc:fabric-loom:1.11.8")
	implementation("net.fabricmc:tiny-remapper:0.11.1")
	implementation("io.github.prcraftmc:class-diff:1.0-SNAPSHOT")
}
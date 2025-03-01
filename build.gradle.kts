@file:Suppress("UnstableApiUsage")

import proguard.ConfigurationParser
import proguard.ProGuard

plugins {
	id("java")
	id("idea")
	id("net.neoforged.moddev")
	id("xyz.wagyourtail.commons-gradle")
	id("org.jetbrains.gradle.plugin.idea-ext")
}

setup()

repositories {
	maven("https://maven.createmod.net")
	maven("https://maven.tterrag.com")
	maven("https://mvn.devos.one/snapshots")
	maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven")
}

neoForge {
	version = "minecraft_version"().removePrefix("1.") + '.' + "forge_version"()

	runs {
		create("client") {
			client()
		}
	}

	parchment {
		minecraftVersion = "minecraft_version"()
		mappingsVersion = "2024.11.17"
	}
}

dependencies {
	implementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_version"()}") { isTransitive = false }
	implementation("net.createmod.ponder:Ponder-NeoForge-${"minecraft_version"()}:${"ponder_version"()}")
	compileOnly("dev.engine-room.flywheel:flywheel-neoforge-api-${"minecraft_version"()}:${"flywheel_version"()}")
	runtimeOnly("dev.engine-room.flywheel:flywheel-neoforge-${"minecraft_version"()}:${"flywheel_version"()}")
	implementation("com.tterrag.registrate:Registrate:${"registrate_version"()}")
}

val compressJar by tasks.registering<ProcessJar> {
	input.set(tasks.jar.get().archiveFile)
	description = "Compresses the merged jar"

	archiveBaseName = "archives_base_name"()
	archiveVersion = "modVersion"()
	archiveClassifier = ""

//	addFileProcessor(extensions = setOf("json", "mcmeta"), processor = Compressors.json)
//	addFileProcessor(extensions = setOf("jar"), processor = Compressors.storeJars)
}

tasks.assemble {
	dependsOn(compressJar)
}

fun setup() {
	val buildNumber: String? = System.getenv("GITHUB_RUN_NUMBER")

	if(git.exists()) {
		println("Current branch: ${git.currentBranch()}")
		println("Current commit: ${git.hash()}")
		if (git.isDirty()) {
			var changes = git.getUncommitedChanges().split("\n").toMutableList()
			val size = changes.size
			val maxChanges = "git_max_changes"().toInt()
			if (size > maxChanges) {
				changes = changes.subList(0, maxChanges)
				changes.add("... and ${size - maxChanges} more")
			}

			println("Uncommitted changes:\n${changes.joinToString("\n") { "  - $it" }}")
		}
	} else {
		println("No git repository")
	}
	println()

	ext["modVersion"] = "mod_version"() + (buildNumber?.let { "-build.$it" } ?: "")
}

tasks.register<CustomTask>("nukeGradleCaches") {
	dependsOn("clean")
	group = "build"
	description = "Deletes all .gradle directories in the project. WARNING: causes IDEs to freeze for a while."
	outputs.upToDateWhen { false }

	action {
		project.rootProject.allprojects.forEach { p ->
			p.projectDir.resolve(".gradle").let {
				if(it.exists()) {
					it.deleteRecursively()
				}
			}
		}
	}
}

operator fun String.invoke() = rootProject.ext[this] as? String ?: error("No property \"$this\"")
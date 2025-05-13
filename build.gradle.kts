@file:Suppress("UnstableApiUsage")

import proguard.ConfigurationParser
import proguard.ProGuard
import xyz.wagyourtail.commons.gradle.shadow.ShadowJar
import xyz.wagyourtail.unimined.expect.task.ExpectPlatformJar
import xyz.wagyourtail.commons.gradle.sourceSets

plugins {
	id("java")
	id("idea")
	id("fabric-loom")
	id("xyz.wagyourtail.commons-gradle")
	id("org.jetbrains.gradle.plugin.idea-ext")
	id("xyz.wagyourtail.unimined.expect-platform")
}

setup()

allprojects {
	apply {
		plugin("java")
		plugin("idea")
		plugin("xyz.wagyourtail.commons-gradle")
		plugin("org.jetbrains.gradle.plugin.idea-ext")
		plugin("xyz.wagyourtail.unimined.expect-platform")
	}

	base.archivesName.set("archives_base_name"())
	version = "modVersion"()
	group = "maven_group"()

	java.toolchain {
		languageVersion.set(JavaLanguageVersion.of("java_version"()))
	}

	idea {
		module.isDownloadSources = true
	}

	repositories {
		maven("https://maven.parchmentmc.org")
		exclusiveContent {
			forRepository { maven("https://api.modrinth.com/maven") }
			filter { includeGroup("maven.modrinth") }
		}
		exclusiveContent {
			forRepository { maven("https://cursemaven.com") }
			filter { includeGroup("curse.maven") }
		}
		maven("https://repo.spongepowered.org/maven")
		maven("https://maven.wagyourtail.xyz/releases")
		maven("https://maven.createmod.net")
		maven("https://maven.tterrag.com")
	}

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.compilerArgs.addAll(listOf("-Xplugin:Manifold no-bootstrap", "-implicit:none"))
		options.forkOptions.memoryMaximumSize = "4g" // what did i do to make this necessary...
	}

	tasks.withType<AbstractArchiveTask> {
		isPreserveFileTimestamps = false
		isReproducibleFileOrder = true
		includeEmptyDirs = false
	}

	dependencies {
		compileOnly("systems.manifold:manifold-props:${"manifold_version"()}") {
			annotationProcessor(this)
		}
		compileOnly("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")

		compileOnly(expectPlatform.annotationsDep)
	}
}

subprojects {
	val platform = project.name.lowercase()

	dependencies {
		implementation(rootProject.sourceSets["main"].output)
	}

	tasks.processResources {
		from(rootProject.sourceSets["main"].resources)

		val props = mapOf(
			"mod_version" to "modVersion"(),
			"minecraft_versions" to multiversion.minecraftVersions.joinToString(
				separator = when(platform) {
					"forge" -> "],["
					"fabric" -> "\",\""
					else -> error("Unknown platform $platform")
				}
			),
			"fabric_version" to "fabric_version"(),
			"create_version" to "minimum_create_version"(),
		)

		inputs.properties(props)

		filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml")) {
			expand(props)
		}
	}

	tasks.jar {
		archiveClassifier = "$platform-dev-unmapped"
		from(rootProject.sourceSets["main"].output) {
			include("**/*.class")
		}
		putInDevlibs()
	}

	val sourcesJar by tasks.registering<Jar> {
		archiveClassifier = "sources"
		from(rootProject.sourceSets["main"].allSource)
		from(sourceSets["main"].allSource)
		putInDevlibs()
	}

	val expectPlatformJar by tasks.registering<ExpectPlatformJar> {
		notCompatibleWithConfigurationCache("oopsies!")
		group = "build"
		platformName = platform
		archiveClassifier = "expect-$platform"
		putInDevlibs()
		inputFiles = files(tasks.jar.get().archiveFile)
	}
}

// disable root jar - subprojects will pull directly from compileJava
tasks.jar { enabled = false }

repositories {
	maven("https://mvn.devos.one/releases")
	maven("https://mvn.devos.one/snapshots")
	maven("https://maven.cafeteria.dev/releases")
	maven("https://maven.jamieswhiteshirt.com/libs-release")
	maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven")
}

val shadow: Configuration by configurations.creating

dependencies {
	minecraft("com.mojang:minecraft:${"minecraft_version"()}")
	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-${"minecraft_version"()}:${"parchment_version"()}@zip")
	})
	modCompileOnly("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric_version"().split("$$").joinToString("+mc${"minecraft_version"()}-build.")}")

	shadow("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")
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

	tasks.assemble {
		subprojects.forEach {
			this.dependsOn(it.tasks.named("assemble"))
		}
	}

	multiversion.findAndLoadProperties()
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
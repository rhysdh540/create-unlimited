@file:Suppress("UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import org.gradle.internal.extensions.stdlib.capitalized
import xyz.wagyourtail.unimined.expect.task.ExpectPlatformJar

plugins {
	id("fabric-loom")
}

repositories {
	devOS("releases")
	devOS("snapshots")
	maven("Cafeteria", "https://maven.cafeteria.dev/releases")
	maven("JamiesWhiteShirt", "https://maven.jamieswhiteshirt.com/libs-release")
	maven("TerraformersMC", "https://maven.terraformersmc.com/releases")
	fuzs()
}

loom {
	runs.all {
		property("mixin.debug.export", "true")
		vmArgs(expectPlatform.getAgentArgs(project.name))
		ideConfigGenerated(true)
		appendProjectPathToConfigName = false

		configName = "${project.name.capitalized()} ${name.capitalized()}"
	}

	mixin.useLegacyMixinAp.set(false)
}

dependencies {
	implementation(rootProject.sourceSets["main"].output)
	minecraft("com.mojang:minecraft:${"minecraft_version"()}")
	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-${"minecraft_version"()}:${"parchment_version"()}@zip")
	})

	modImplementation("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric_version"().split("$$").joinToString("+mc${"minecraft_version"()}-build.")}")
	modImplementation("net.fabricmc:fabric-loader:${"fabric_version"()}")

	modImplementation("com.terraformersmc:modmenu:${"modmenu_version"()}")
	modLocalRuntime("net.fabricmc.fabric-api:fabric-api-deprecated:${"fabric_api_version"()}+${"minecraft_version"()}")
}

tasks.jar {
	putInDevlibs()
	from(rootProject.sourceSets["main"].output)
}

val expectPlatformJar by tasks.registering<ExpectPlatformJar> {
	putInDevlibs()
	group = "build"
	inputFiles = files(tasks.jar.get().archiveFile)
	platformName = "fabric"
	archiveClassifier.set("expect")
}

tasks.shadowJar {
	clearSourcePaths()
	putInDevlibs()
	archiveClassifier = "shadow"

	configurations.empty()
	from(zipTree(expectPlatformJar.flatMap { it.archiveFile }))

	relocate(SimpleRelocator(
		pattern = "dev.rdh.createunlimited",
		shadedPattern = "dev.rdh.createunlimited.fabric",
		includes = listOf("dev.rdh.createunlimited.**"),
		excludes = listOf("dev.rdh.createunlimited.fabric.**")
	))

	eachFile {
		val oldMixinPackage = "dev.rdh.createunlimited"
		val newMixinPackage = "dev.rdh.createunlimited.fabric"
		if (name.endsWith("mixins.json")) {
			name = "${name.removeSuffix(".mixins.json")}-fabric.mixins.json"
			filter {
				it.replace(oldMixinPackage, newMixinPackage)
			}
		}

		if (name.equals("fabric.mod.json")) {
			filter {
				it.replace(
					"createunlimited.mixins.json",
					"createunlimited-fabric.mixins.json"
				)
			}
		}
	}
}

tasks.remapJar {
	inputFile.set(tasks.shadowJar.map { it.archiveFile.get() })

	if (System.getenv("CI")?.toBoolean() == true) {
		destinationDirectory.set(rootProject.file("artifacts"))
	}
}

tasks.assemble {
	dependsOn(tasks.remapJar)
}

tasks.processResources {
	val props = mapOf(
		"mod_version" to rootProject.version,
		"minecraft_versions" to multiversion.minecraftVersions.joinToString("\",\""),
		"fabric_version" to "fabric_version"(),
		"create_version" to "minimum_create_version"(),
	)

	inputs.properties(props)

	filesMatching("fabric.mod.json") {
		expand(props)
	}
}

operator fun String.invoke() = prop(this)
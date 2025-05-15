import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import net.neoforged.moddevgradle.internal.RunGameTask
import net.neoforged.moddevgradle.legacyforge.tasks.RemapJar
import org.gradle.kotlin.dsl.withType
import xyz.wagyourtail.unimined.expect.task.ExpectPlatformJar

plugins {
	id("net.neoforged.moddev.legacyforge")
}

legacyForge {
	version = "${"minecraft_version"()}-${"forge_version"()}"

	parchment {
		minecraftVersion = "minecraft_version"()
		mappingsVersion = "parchment_version"()
	}

	runs {
		create("client") {
			client()
		}

		create("server") {
			server()
		}

		all {
			systemProperty("mixin.debug.export", "true")
			systemProperty("mixin.env.remapRefMap", "true")
			systemProperty("mixin.env.refMapRemappingFile", project.layout.buildDirectory.map { it.file("moddev/artifacts/intermediateToNamed.srg") }.get().asFile.absolutePath)
		}
	}
}

mixin {
	config("createunlimited.mixins.json")
}

dependencies {
	implementation(rootProject.sourceSets["main"].output)
	modImplementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge_version"()}:slim") { isTransitive = false }
	modImplementation("net.createmod.ponder:Ponder-Forge-${"minecraft_version"()}:${"ponder_version"()}")
	modImplementation("com.tterrag.registrate:Registrate:${"registrate_version"()}")
	modCompileOnly("dev.engine-room.flywheel:flywheel-forge-api-${"minecraft_version"()}:${"flywheel_version"()}")
	modRuntimeOnly("dev.engine-room.flywheel:flywheel-forge-${"minecraft_version"()}:${"flywheel_version"()}")
	modImplementation("io.github.llamalad7:mixinextras-forge:${"mixin_extras_version"()}")
}

tasks.jar {
	putInDevlibs()
	from(rootProject.sourceSets["main"].output)
}

val expectPlatformJar by tasks.registering<ExpectPlatformJar> {
	putInDevlibs()
	group = "build"
	inputFiles = files(tasks.getByName<RemapJar>("reobfJar").archiveFile)
	platformName = "forge"
	archiveClassifier.set("expect")
}

tasks.shadowJar {
	clearSourcePaths()
	archiveClassifier = null

	configurations.empty()
	from(zipTree(expectPlatformJar.get().archiveFile))

	relocate(SimpleRelocator(
		pattern = "dev.rdh.createunlimited",
		shadedPattern = "dev.rdh.createunlimited.forge",
		includes = listOf("dev.rdh.createunlimited.**"),
		excludes = listOf("dev.rdh.createunlimited.forge.**")
	))

	eachFile {
		val oldMixinPackage = "dev.rdh.createunlimited.asm.mixin"
		val newMixinPackage = "dev.rdh.createunlimited.forge.asm.mixin"
		if (name.endsWith(".mixins.json")) {
			filter {
				it.replace(oldMixinPackage, newMixinPackage)
			}
		}
		if (name.endsWith(".refmap.json")) {
			filter {
				it.replace(
					oldMixinPackage.replace('.', '/'),
					newMixinPackage.replace('.', '/')
				)
			}
		}
	}

	manifest.attributes(
		"MixinConfigs" to mixin.configs.get().joinToString(",")
	)
}

tasks.assemble {
	dependsOn(tasks.shadowJar)
}

tasks.processResources {
	val props = mapOf(
		"mod_version" to rootProject.version,
		"minecraft_versions" to multiversion.minecraftVersions.joinToString("],["),
		"create_version" to "minimum_create_version"(),
	)

	inputs.properties(props)

	filesMatching("META-INF/mods.toml") {
		expand(props)
	}
}

afterEvaluate {
	tasks.withType<RunGameTask>().configureEach {
		expectPlatform.insertAgent(this, "forge")
	}
}

operator fun String.invoke() = prop(this)
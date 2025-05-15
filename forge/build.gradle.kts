import net.neoforged.moddevgradle.internal.RunGameTask
import org.gradle.kotlin.dsl.withType

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

dependencies {
	implementation(rootProject.sourceSets["main"].output)
	modImplementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge_version"()}:slim") { isTransitive = false }
	modImplementation("net.createmod.ponder:Ponder-Forge-${"minecraft_version"()}:${"ponder_version"()}")
	modImplementation("com.tterrag.registrate:Registrate:${"registrate_version"()}")
	modCompileOnly("dev.engine-room.flywheel:flywheel-forge-api-${"minecraft_version"()}:${"flywheel_version"()}")
	modRuntimeOnly("dev.engine-room.flywheel:flywheel-forge-${"minecraft_version"()}:${"flywheel_version"()}")
	modImplementation("io.github.llamalad7:mixinextras-forge:${"mixin_extras_version"()}")
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
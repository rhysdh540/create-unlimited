import net.fabricmc.loom.util.gradle.SourceSetReference

plugins {
	id("fabric-loom")
}

repositories {
	exclusiveMaven(
		name = "ParchmentMC",
		url = "https://maven.parchmentmc.org"
	) {
		includeGroupAndSubgroups("org.parchmentmc")
	}

	exclusiveMaven(
		name = "TerraformersMC",
		url = "https://maven.terraformersmc.com"
	) {
		includeGroupAndSubgroups("com.terraformersmc")
	}

	exclusiveMaven(
		name = "Fuzs Mod Resources",
		url = "https://raw.githubusercontent.com/Fuzss/modresources/main/maven"
	) {
		includeGroupAndSubgroups("fuzs")
	}

	maven("Cafeteria", "https://maven.cafeteria.dev/releases")
	maven("JamiesWhiteShirt", "https://maven.jamieswhiteshirt.com/libs-release") {
		includeGroupAndSubgroups("com.jamieswhiteshirt")
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${"minecraft_version"()}")
	mappings(loom.layered {
		officialMojangMappings()
		"parchment_version".maybe {
			parchment("org.parchmentmc.data:parchment-${"minecraft_version"()}:${it}@zip")
		}
	})

	modImplementation("net.fabricmc:fabric-loader:${"fabric_version"()}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${"fabric_version"()}")

	modImplementation("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_version"().split("$$").joinToString("+mc${"minecraft_version"()}-build.")}")
	modImplementation("com.terraformersmc:modmenu:${"modmenu_version"()}")
	modLocalRuntime("net.fabricmc.fabric-api:fabric-api-deprecated:${"fabric_api_version"()}+${"minecraft_version"()}")
}

loom {
	mods {
		maybeRegister("create-unlimited") {
			modSourceSets.add(sourceSets.main.map { SourceSetReference(it, project) })
		}
	}

	runs {
		maybeRegister("client") {
			client()
			name("Fabric Client: " + "minecraft_version"())
		}

		maybeRegister("server") {
			server()
			name("Fabric Server: " + "minecraft_version"())
		}

		configureEach {
			ideConfigGenerated(true)
			appendProjectPathToConfigName = false
		}
	}

	mixin {
		useLegacyMixinAp = false
	}
}

tasks.processResources {
	filesMatching("META-INF/*.toml") {
		exclude()
	}
}

operator fun String.invoke() = prop(this)
fun String.maybe(block: (String) -> Unit) = propMaybe(this)?.let(block)
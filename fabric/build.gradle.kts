import net.fabricmc.loom.task.RunGameTask

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
	}
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

afterEvaluate {
	tasks.withType<RunGameTask>().configureEach {
		expectPlatform.insertAgent(this, "fabric")
	}
}

operator fun String.invoke() = prop(this)
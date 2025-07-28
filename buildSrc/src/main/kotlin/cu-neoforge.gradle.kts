plugins {
	id("net.neoforged.moddev")
}

neoForge {
	version = "minecraft_version"().removePrefix("1.") + "." + "forge_version"()
}

apply(plugin = "cu-forge-common")

tasks.processResources {
	filesMatching("META-INF/mods.toml") {
		name = "neoforge.mods.toml"
	}

	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

operator fun String.invoke() = prop(this)
operator fun String.invoke(): String {
	return rootProject.ext[this] as? String
		?: throw IllegalStateException("Property $this is not defined")
}

apply(plugin = "com.github.johnrengelman.shadow")
apply(plugin = "platform")

architectury.fabric()

configurations {
	"developmentFabric" {
		extendsFrom(getByName("common"))
	}
}

dependencies {
	modImplementation("net.fabricmc:fabric-loader:${"fabric"()}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${"fabric_api"()}+${"minecraft_version"()}")

	modImplementation("com.terraformersmc:modmenu:${"modmenu"()}")

	modImplementation("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric"()}+mc${"minecraft_version"()}") {
		exclude(group = "com.github.llamalad7.mixinextras", module = "mixinextras-fabric")
	}

	// have deprecated modules present at runtime only
	modLocalRuntime("net.fabricmc.fabric-api:fabric-api-deprecated:${"fabric_api"()}+${"minecraft_version"()}")

	// Dev Env Optimizations
	if (rootProject.hasProperty("lazydfu")) {
		modRuntimeOnly("maven.modrinth:lazydfu:${"lazydfu"()}")
	}
}

tasks.remapJar {
	injectAccessWidener.set(true)
}

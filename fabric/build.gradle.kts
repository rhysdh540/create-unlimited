import xyz.wagyourtail.unimined.api.unimined

val modLocalRuntime: Configuration by configurations.creating {
	configurations.runtimeClasspath.get().extendsFrom(this)
	isCanBeConsumed = false
	isCanBeResolved = true
}

unimined.minecraft {
	fabric { loader("fabric_version"()) }

	mods {
		remap(modLocalRuntime)

		modImplementation {
			catchAWNamespaceAssertion()
		}
	}
}

repositories {
	maven("https://maven.tterrag.com")
	maven("https://mvn.devos.one/snapshots")
	maven("https://maven.cafeteria.dev/releases")
	maven("https://maven.jamieswhiteshirt.com/libs-release")
	maven("https://maven.theillusivec4.top")
	maven("https://jitpack.io")
	maven("https://maven.terraformersmc.com")
}

dependencies {
	modImplementation("net.fabricmc.fabric-api:fabric-api:${"fabric_api_version"()}+${"minecraft_version"()}")

	modImplementation("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric_version"()}+mc${"minecraft_version"()}") {
		exclude(group = "com.github.llamalad7.mixinextras", module = "mixinextras-fabric")
	}

	modImplementation("com.terraformersmc:modmenu:${"modmenu_version"()}")

	// have deprecated modules present at runtime only
	if("minecraft_version"() != "1.18.2") {
		modLocalRuntime("net.fabricmc.fabric-api:fabric-api-deprecated:${"fabric_api_version"()}+${"minecraft_version"()}")
	}

	// Dev Env Optimizations
	if (rootProject.hasProperty("lazydfu_version")) {
		modLocalRuntime("maven.modrinth:lazydfu:${"lazydfu_version"()}")
	}
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("Property $this is not defined")
architectury {
	common {
		for(p in rootProject.subprojects.filter { it != project }) {
			add(p.name)
		}
	}
}

dependencies {
	modCompileOnly("net.fabricmc:fabric-loader:${"fabric"()}")

	modCompileOnly("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric"()}+mc${"minecraft_version"()}") {
		exclude(group = "com.github.llamalad7.mixinextras", module = "mixinextras-fabric")
		exclude(group = "net.fabricmc.fabric-api") // fabric access wideners are not safe to use
	}
}

tasks.processResources {
	from(rootProject.file("LICENSE")) {
		rename { "${it}_${"archives_base_name"()}" }
	}
}

operator fun String.invoke(): String = rootProject.ext[this] as? String
	?: error("Property $this is not defined")

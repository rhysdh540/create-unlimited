architectury {
	common {
		for(p in rootProject.subprojects) {
			if(p != project) add(p.name)
		}
	}
}

dependencies {
	modCompileOnly("net.fabricmc:fabric-loader:${"fabric"()}")

	modCompileOnly("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric"()}+mc${"minecraft_version"()}") {
		exclude(group = "com.github.llamalad7.mixinextras", module = "mixinextras-fabric")
	}

	modCompileOnly("net.fabricmc.fabric-api:fabric-api:${"fabric_api"()}+${"minecraft_version"()}")
}

tasks.processResources {
	from(rootProject.file("LICENSE")) {
		rename { "${it}_${"archives_base_name"()}" }
	}
}

operator fun String.invoke(): String {
	return rootProject.ext[this] as? String
		?: throw IllegalStateException("Property $this is not defined")
}

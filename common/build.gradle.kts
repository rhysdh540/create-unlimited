import xyz.wagyourtail.unimined.api.unimined

dependencies {
	"modImplementation"("net.fabricmc.fabric-api:fabric-api:${"fabric_api"()}+${"minecraft_version"()}")
	"modImplementation"("com.terraformersmc:modmenu:${"modmenu"()}")
}

unimined.minecraft {
	fabric {
		loader("0.15.3")
	}

	defaultRemapJar = false

	mods.modImplementation {
		catchAWNamespaceAssertion()
	}
}

operator fun String.invoke(): String {
	return rootProject.ext[this] as? String
		?: throw IllegalStateException("Property $this is not defined")
}
import xyz.wagyourtail.unimined.api.unimined

unimined.minecraft {
	fabric {
		loader("fabric"())
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
import xyz.wagyourtail.unimined.api.unimined

unimined.minecraft {
	minecraftForge {
		loader("forge"())
		mixinConfig("createunlimited.mixins.json")
	}

	defaultRemapJar = true
}

operator fun String.invoke(): String {
	return rootProject.ext[this] as? String
		?: throw IllegalStateException("Property $this is not defined")
}
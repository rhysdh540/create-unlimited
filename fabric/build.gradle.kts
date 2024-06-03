import xyz.wagyourtail.unimined.api.unimined

unimined.minecraft {
	fabric { loader("fabric_version"()) }

	mappings {
		intermediary()

		devFallbackNamespace("intermediary")
	}
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("Property $this is not defined")
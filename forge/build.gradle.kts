import xyz.wagyourtail.unimined.api.unimined

dependencies {
	implementation(project(":common"))
}

unimined.minecraft {
	minecraftForge {
		loader("forge"())
		mixinConfig("createunlimited.mixins.json")
	}

	defaultRemapJar = true
}

tasks.processResources {
	from(project(":common").sourceSets["main"].resources)
}

tasks.withType<JavaCompile> {
	source(project(":common").sourceSets["main"].java.srcDirs)
}

operator fun String.invoke(): String {
	return rootProject.ext[this] as? String
		?: throw IllegalStateException("Property $this is not defined")
}
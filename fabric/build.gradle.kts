import xyz.wagyourtail.unimined.api.unimined

unimined.minecraft {
	fabric {
		loader("fabric"())
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
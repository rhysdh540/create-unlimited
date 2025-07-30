plugins {
	id("idea")
    id("xyz.wagyourtail.manifold")
}

idea {
	module {
		isDownloadJavadoc = true
		isDownloadSources = true
	}
}

repositories {
	maven(
		name = "devOS Releases",
		url = "https://mvn.devos.one/releases"
	)

	maven(
		name = "devOS Snapshots",
		url = "https://mvn.devos.one/snapshots"
	)

	maven(
		name = "CreateMod",
		url = "https://maven.createmod.net"
	) {
		includeGroupAndSubgroups("net.createmod")
		includeGroupAndSubgroups("dev.engine-room")
	}
}

run {
    val (mc, platform) = stonecutter.current.project.split('-', limit = 2)
    ext["minecraft_version"] = mc
    ext["platform"] = platform

    project.group = rootProject.group
    project.base.archivesName.set(rootProject.base.archivesName.map { "$it-$mc-$platform" })
    project.version = rootProject.version

	java {
		val javaVersion = if (stonecutter.eval(stonecutter.current.version, ">=1.20.6")) 21 else 17
		toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
	}

	apply(plugin = "cu-${platform}")
}

stonecutter {
    constants {
        match("platform"(), "fabric", "forge", "neoforge")
        put("forgelike", "platform"().let { it == "forge" || it == "neoforge" })
    }
}

manifold {
    version = "manifold_version"()
    pluginArgs.add("--no-bootstrap")

    preprocessor {
        config {
            property("MC", stonecutter.current.version.removePrefix("1."))
            stonecutter.constants.forEach {
                if (it.value) { property(it.key) }
            }
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
    includeEmptyDirs = false
}

tasks.processResources {
	inputs.property("mod_version", rootProject.version)
	filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml")) {
		expand("mod_version" to inputs.properties["mod_version"].toString())
	}
}

tasks.named<AbstractArchiveTask>(if ("platform"() == "neoforge") "jar" else "remapJar") {
	destinationDirectory = rootProject.layout.buildDirectory.dir("libs")
}

operator fun String.invoke() = findProperty(this)?.toString() ?: error("No property \"$this\"")
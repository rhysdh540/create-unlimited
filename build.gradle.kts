import dev.architectury.plugin.ArchitectPluginExtension

plugins {
	java
	id("architectury-plugin") apply(false)
	id("dev.architectury.loom") apply(false)
	id("com.github.johnrengelman.shadow") apply(false)

	id("io.github.pacifistmc.forgix")

	id("properties") apply(false)
	id("subprojects") apply(false)
	id("platform") apply(false)
	id("postprocessor") apply(false)
}
setup()
setupForgix()

operator fun String.invoke(): String {
	return rootProject.ext[this] as? String
		?: throw IllegalStateException("Property $this is not defined")
}

allprojects {
	apply(plugin = "java")

	base.archivesName.set("archives_base_name"())
	version = "modVersion"()
	group = "maven_group"()

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.release.set(17)
		options.compilerArgs.addAll(listOf("-Xplugin:Manifold", "-implicit:none"))
	}

	tasks.withType<AbstractArchiveTask> {
		setPreserveFileTimestamps(false)
		setReproducibleFileOrder(true)
	}
}

extensions.getByType<ArchitectPluginExtension>().apply {
	minecraft = "minecraft_version"()
}

tasks.clean {
	delete(".architectury-transformer")
}

tasks.jar {
	enabled = false
}

subprojects {
	apply(plugin = "subprojects")
}

fun setup() {
	println("Create Unlimited v${"mod_version"()}")
	val buildNumber = System.getenv("GITHUB_RUN_NUMBER")
	if(buildNumber != null) {
		println("Build #$buildNumber")
	}
	println()
	ext["modVersion"] = "mod_version"() + (buildNumber?.let { "-build.$it" } ?: "")

	apply(plugin = "properties")

	println("\nPlugin versions:")
	apply(plugin = "architectury-plugin")

	tasks.register("nukeGradleCaches") {
		dependsOn("clean")
		group = "build"
		doLast {
			allprojects.forEach {
				it.file(".gradle").deleteRecursively()
			}
		}
	}
}

fun setupForgix() {
	forgix {
		group = "maven_group"()
		mergedJarName = "createunlimited-${"modVersion"()}.jar"
		outputDir = "build/libs/merged"

		removeDuplicate("dev.rdh.createunlimited.shadow.mixinextras")
	}

	tasks.mergeJars {
		dependsOn("assemble")
	}

	tasks.assemble {
		subprojects.forEach {
			this.dependsOn(it.tasks.named("assemble"))
		}
		finalizedBy("mergeJars")
	}

	apply(plugin = "postprocessor")
}

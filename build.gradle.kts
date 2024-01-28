import dev.architectury.plugin.ArchitectPluginExtension
import io.github.p03w.machete.config.MachetePluginExtension
import io.github.p03w.machete.tasks.OptimizeJarsTask

plugins {
	java
	id("architectury-plugin") apply(false)
	id("dev.architectury.loom") apply(false)
	id("com.github.johnrengelman.shadow") apply(false)

	id("io.github.pacifistmc.forgix")
	id("io.github.p03w.machete")

	id("properties") apply(false)
	id("subprojects") apply(false)
	id("platform") apply(false)
}
setup()
setupForgixAndMachete()

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

tasks.clean.configure {
	delete(".architectury-transformer")
}

tasks.jar.configure {
	enabled = false
}

subprojects {
	apply(plugin = "subprojects")
}

tasks.assemble.configure {
	finalizedBy("mergeJars")
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

fun setupForgixAndMachete() {
	forgix {
		group = "maven_group"()
		mergedJarName = "createunlimited-${"modVersion"()}.jar"
		outputDir = "build/libs/merged"

		removeDuplicate("dev.rdh.createunlimited.shadow.mixinextras")
	}

	tasks.assemble.configure {
		subprojects.forEach {
			this.dependsOn(it.tasks.named("assemble"))
		}
		finalizedBy("mergeJars")
	}

	machete {
		ignoredTasks.add("jar")

		jij.enabled = false
		png.enabled = false
		json.enabled = true
	}

	tasks.register<OptimizeJarsTask>("optimizeMergeJars") {
		dependsOn("mergeJars")

		buildDir.set(project.layout.buildDirectory.get().asFile)
		extension = project.extensions.getByType<MachetePluginExtension>()
		inputs.file(forgix.outputDir + File.separator + forgix.mergedJarName)
	}

	tasks.mergeJars.configure {
		finalizedBy("optimizeMergeJars")
	}
}

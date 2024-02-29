import xyz.wagyourtail.unimined.api.unimined

plugins {
	java
	id("xyz.wagyourtail.unimined") apply(false)
	id("com.github.johnrengelman.shadow") apply(false)
	id("properties") apply(false)
	id("postprocessor")
}
setup()

allprojects {
	apply(plugin = "java")

	base.archivesName.set("archives_base_name"())
	version = "modVersion"()
	group = "maven_group"()

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.release = 17
		options.compilerArgs.addAll(listOf("-Xplugin:Manifold", "-implicit:none"))
	}

	tasks.withType<AbstractArchiveTask> {
		isPreserveFileTimestamps = false
		isReproducibleFileOrder = true
	}

	setupRepos()
}

tasks.jar {
	enabled = false
}

subprojects {
	apply(plugin = "xyz.wagyourtail.unimined")
	apply(plugin = "com.github.johnrengelman.shadow")

	val localRuntime: Configuration by configurations.creating {
		isCanBeResolved = true
		isCanBeConsumed = false
	}
	val modLocalRuntime: Configuration by configurations.creating {
		extendsFrom(localRuntime)
	}

	val shade: Configuration by configurations.creating
	configurations["implementation"].extendsFrom(shade)

	unimined.minecraft(sourceSets["main"], lateApply = true) {
		version = "minecraft_version"()

		mappings {
			intermediary()
			quilt() // for javadocs/parameters that parchment doesn't have
			mojmap()
			parchment(version = "parchment"())

			devNamespace("mojmap")
		}

		mods {
			remap(modLocalRuntime)
		}

		runs {
			config("server") {
				disabled = true
			}
		}
	}

	dependencies {
		"systems.manifold:manifold-props:${"manifold_version"()}".also {
			annotationProcessor(it)
			compileOnly(it)
		}
		localRuntime("systems.manifold:manifold-ext-rt:${"manifold_version"()}")

		"io.github.llamalad7:mixinextras-common:${"mixin_extras"()}".also {
			annotationProcessor(it)
			compileOnly(it)

			if(this == project(":common")) {
				shade(it)
			}
		}
	}
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

	tasks.register("nukeGradleCaches") {
		dependsOn("clean")
		group = "build"
		doLast {
			allprojects.forEach {
				it.file(".gradle").deleteRecursively()
			}
		}
	}

	tasks.assemble {
		subprojects.forEach {
			this.dependsOn(it.tasks.named("assemble"))
		}
	}
}

operator fun String.invoke(): String {
	return rootProject.ext[this] as? String
		?: throw IllegalStateException("Property $this is not defined")
}

fun Project.setupRepos() {
	repositories {
		mavenCentral {
			content {
				excludeGroup("ca.weblite")
			}
		}

		maven("https://maven.parchmentmc.org")
		maven("https://maven.quiltmc.org/repository/release")
		maven("https://maven.fabricmc.net")
		maven("https://maven.minecraftforge.net")
		maven("https://maven.ithundxr.dev/releases")
		maven("https://mvn.devos.one/snapshots")
		maven("https://maven.cafeteria.dev/releases")
		maven("https://maven.jamieswhiteshirt.com/libs-release")
		maven("https://maven.theillusivec4.top")
		maven("https://maven.terraformersmc.com/releases") {
			content {
				includeGroup("com.terraformersmc.modmenu")
			}
		}
		maven("https://jitpack.io")
		maven("https://maven.tterrag.com") {
			content {
				includeGroup("com.simibubi.create")
				includeGroup("com.jozufozu.flywheel")
				includeGroup("com.tterrag.registrate")
			}
		}

		exclusiveContent {
			forRepository { maven("https://api.modrinth.com/maven") }
			filter {
				includeGroup("maven.modrinth")
			}
		}

		exclusiveContent {
			forRepository { maven("https://cursemaven.com") }
			filter {
				includeGroup("curse.maven")
			}
		}
	}
}

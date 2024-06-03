import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask
import xyz.wagyourtail.unimined.util.sourceSets

plugins {
	id("xyz.wagyourtail.unimined")
	id("com.github.johnrengelman.shadow")
}
try {
	Git.repository = rootDir.toPath()
} catch(_: IllegalStateException) {
}

setup()

allprojects {
	apply(plugin = "java")
	apply(plugin = "xyz.wagyourtail.unimined")
	apply(plugin = "com.github.johnrengelman.shadow")

	base.archivesName.set("archives_base_name"())
	version = "modVersion"()
	group = "maven_group"()

	java {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	repositories {
		mavenCentral {
			content {
				excludeGroup("ca.weblite")
			}
		}
		maven("https://maven.parchmentmc.org")
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

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.release = 17
		options.compilerArgs.addAll(listOf("-Xplugin:Manifold no-bootstrap", "-implicit:none"))
	}

	tasks.withType<AbstractArchiveTask> {
		isPreserveFileTimestamps = false
		isReproducibleFileOrder = true
	}

	unimined.minecraft(sourceSet = sourceSets["main"], lateApply = true) {
		version = "minecraft_version"()

		mappings {
			mojmap()
			parchment(version = "parchment_version"())
		}
	}

	tasks.withType<RemapJarTask> {
		mixinRemap {
			enableMixinExtra()
			disableRefmap()
		}
	}

	dependencies {
		compileOnly("systems.manifold:manifold-props:${"manifold_version"()}") {
			annotationProcessor(this)
		}
	}
}

subprojects {
	tasks.processResources {
		from(rootProject.sourceSets["main"].resources)

		val props = mapOf(
			"mod_version" to "modVersion"(),
		)

		inputs.properties(props)

		filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml")) {
			expand(props)
		}
	}

	tasks.withType<JavaCompile> {
		source(rootProject.sourceSets["main"].allSource)
	}

	dependencies {
		implementation(rootProject).apply {
			(this as ModuleDependency).isTransitive = false
		}
	}
}

unimined.minecraft {
	fabric { loader("fabric_version"()) }

	defaultRemapJar = false

	mods {
		modImplementation {
			catchAWNamespaceAssertion()
		}
	}
}

repositories {
	maven("https://maven.tterrag.com")
	maven("https://mvn.devos.one/snapshots")
	maven("https://maven.cafeteria.dev/releases")
	maven("https://maven.jamieswhiteshirt.com/libs-release")
	maven("https://maven.theillusivec4.top")
	maven("https://jitpack.io")
}

dependencies {
	"modImplementation"("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric_version"()}+mc${"minecraft_version"()}") {
		exclude(group = "com.github.llamalad7.mixinextras", module = "mixinextras-fabric")
	}
}

fun setup() {
	println("Create Unlimited v${"mod_version"()}")

	val buildNumber: String? = System.getenv("GITHUB_RUN_NUMBER")
	if(buildNumber != null) {
		println("Build #$buildNumber")
	}
	println()
	println("Current branch: ${Git.currentBranch()}")
	println("Current commit: ${Git.hash()}")
	if(Git.isDirty()) {
		var changes = Git.getUncommitedChanges().split("\n").toMutableList()
		val maxChanges = 10
		if(changes.size > maxChanges) {
			changes = changes.subList(0, maxChanges)
			changes.add("... and ${changes.size - maxChanges} more")
		}

		changes.replaceAll { "  - $it" }

		println("Uncommitted changes:\n${changes.joinToString("\n")}")
	}
	println()

	ext["modVersion"] = "mod_version"() + (buildNumber?.let { "-build.$it" } ?: "")

	findAndLoadProperties()
}

tasks.register("nukeGradleCaches") {
	dependsOn("clean")
	group = "build"
	description = "Deletes all .gradle directories in the project. WARNING: causes IDEs to freeze for a while."
	doLast {
		fileTree(rootDir) {
			include("**/.gradle")
		}.forEach {
			it.deleteRecursively()
		}
	}
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("Property $this is not defined")
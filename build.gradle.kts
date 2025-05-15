plugins {
	id("java")
	id("idea")
	id("com.gradleup.shadow")
	id("org.jetbrains.gradle.plugin.idea-ext")
	id("xyz.wagyourtail.unimined.expect-platform")

	id("fabric-loom")
	id("net.neoforged.moddev.legacyforge") apply(false)
}

setup()

allprojects {
	apply {
		plugin("java")
		plugin("idea")
		plugin("com.gradleup.shadow")
		plugin("org.jetbrains.gradle.plugin.idea-ext")
		plugin("xyz.wagyourtail.unimined.expect-platform")
	}

	java.toolchain {
		languageVersion.set(JavaLanguageVersion.of("java_version"()))
	}

	idea.module {
		isDownloadSources = true
	}

	repositories {
		mavenCentral {
			content {
				excludeGroup("org.lwjgl")
				excludeGroup("com.mojang")
			}
		}

		parchment()
		modrinth()
		curseMaven()
		wagYourMaven("releases")
		createMod()
		tterrag()
	}

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.compilerArgs.addAll(listOf("-Xplugin:Manifold no-bootstrap", "-implicit:none"))
	}

	tasks.withType<AbstractArchiveTask> {
		isPreserveFileTimestamps = false
		isReproducibleFileOrder = true
		includeEmptyDirs = false
	}

	dependencies {
		compileOnly("systems.manifold:manifold-props:${"manifold_version"()}") {
			annotationProcessor(this)
		}
		compileOnly("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")

		compileOnly(expectPlatform.annotationsDep)
	}
}

// disable root jar - subprojects will pull directly from compileJava
tasks.jar { enabled = false }

repositories {
	devOS("releases")
	devOS("snapshots")
	maven("Cafeteria", "https://maven.cafeteria.dev/releases")
	maven("JamiesWhiteShirt", "https://maven.jamieswhiteshirt.com/libs-release")
	fuzs()
	sponge()
}

dependencies {
	minecraft("com.mojang:minecraft:${"minecraft_version"()}")
	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-${"minecraft_version"()}:${"parchment_version"()}@zip")
	})

	modCompileOnly("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric_version"().split("$$").joinToString("+mc${"minecraft_version"()}-build.")}")

	implementation("org.ow2.asm:asm:${"asm_version"()}")
	implementation("org.ow2.asm:asm-tree:${"asm_version"()}")
	implementation("org.ow2.asm:asm-commons:${"asm_version"()}")
	implementation("org.spongepowered:mixin:${"mixin_version"()}")

	shadow("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")
}

fun setup() {
	val buildNumber: String? = System.getenv("GITHUB_RUN_NUMBER")

	if(git.exists()) {
		println("Current branch: ${git.currentBranch()}")
		println("Current commit: ${git.hash()}")
		if (git.isDirty()) {
			var changes = git.getUncommitedChanges().split("\n").toMutableList()
			val size = changes.size
			val maxChanges = "git_max_changes"().toInt()
			if (size > maxChanges) {
				changes = changes.subList(0, maxChanges)
				changes.add("... and ${size - maxChanges} more")
			}

			println("Uncommitted changes:\n${changes.joinToString("\n") { "  - $it" }}")
		}
	} else {
		println("No git repository")
	}
	println()

	group = "maven_group"()
	base.archivesName = "archives_base_name"()
	version = "mod_version"() + (buildNumber?.let { "-build.$it" } ?: "")

	tasks.assemble {
		subprojects.forEach {
			this.dependsOn(it.tasks.named("assemble"))
		}
	}

	multiversion.findAndLoadProperties()
}

val nukeGradleCaches by tasks.registering<CustomTask> {
	dependsOn("clean")
	group = "build"
	description = "Deletes all .gradle directories in the project. WARNING: causes IDEs to freeze for a while."
	outputs.upToDateWhen { false }

	val dirsToDelete = project.rootProject.allprojects.map { it.projectDir.resolve(".gradle") }

	action {
		dirsToDelete.filter { it.exists() }.forEach {
			it.deleteRecursively()
		}
	}
}

operator fun String.invoke() = prop(this)
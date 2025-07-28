@file:Suppress("UnstableApiUsage")

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
		vendor.set(JvmVendorSpec.AZUL)
	}

	idea.module {
		isDownloadSources = true
	}

	repositories {
		mavenCentral {
			name = "MavenCentralLWJGL" // loom does something funny, this turns it off
			content {
				excludeGroup("org.lwjgl")
				excludeGroup("com.mojang")
			}
		}

		parchment()
		modrinth()
		curseMaven()
		wagYourMaven("snapshots").apply {
			content {
				includeGroupAndSubgroups("xyz.wagyourtail")
			}
		}
		createMod()
		tterrag()
	}

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.compilerArgs.addAll(listOf("-Xplugin:Manifold no-bootstrap", "-implicit:none"))
		options.forkOptions.memoryMaximumSize = "4G" // I still do not know why this is necessary
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

subprojects {
	base.archivesName.set(rootProject.base.archivesName.map { "$it-${project.name}" })
	group = rootProject.group
	version = rootProject.version

	expectPlatform {
		stripAnnotations = false
	}
}

// disable root jar - subprojects will pull directly from compileJava
tasks.jar { enabled = false }
tasks.remapJar { enabled = false }
loom {
	mixin.useLegacyMixinAp.set(false)
	runs.clear()
}

repositories {
	mavenLocal()
	devOS("releases")
	devOS("snapshots")
	maven("Cafeteria", "https://maven.cafeteria.dev/releases")
	maven("JamiesWhiteShirt", "https://maven.jamieswhiteshirt.com/libs-release")
	fuzs()
	sponge()
}

dependencies {
	val shade by configurations.creating

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

	implementation("net.fabricmc:sponge-mixin:0.15.5+mixin.0.8.7")
	implementation("org.ow2.asm:asm-tree:9.8")
	"io.github.prcraftmc:class-diff:1.0-SNAPSHOT".let {
		implementation(it)
		shade(it) {
			exclude(group = "org.ow2.asm")
		}
	}
}

tasks.shadowJar {
	archiveClassifier = "common"
	//clearSourcePaths()
	configurations = listOf(project.configurations["shade"])
	putInDevlibs()
	minimize()

	manifest.attributes(
		//"MixinConfigs" to "createunlimited-forge.mixins.json",
		"Git-Commit" to git.hash(long = true),
	)

	if (System.getenv("CI")?.toBoolean() == true) {
		destinationDirectory.set(rootProject.file("artifacts"))
	}

	exclude("**/module-info.class")
	exclude("META-INF/maven/**")

	relocate("com.github.difflib", "dev.rdh.createunlimited.lib.difflib")
	relocate("io.github.prcraftmc.classdiff", "dev.rdh.createunlimited.lib.classdiff")
	relocate("com.nothome.delta", "dev.rdh.createunlimited.lib.delta")
}

tasks.register<MergedJar>("mergeJars") {
	group = "build"

	mainJar.set(tasks.shadowJar.flatMap { it.archiveFile })

	archiveClassifier.set("merged")
}

subprojects.forEach { p ->
	p.afterEvaluate {
		//rootProject.tasks.shadowJar {
		rootProject.tasks.named<MergedJar>("mergeJars") {
			val task = p.tasks.named<org.gradle.jvm.tasks.Jar>("remapJar")
			//from(zipTree(task.flatMap { it.archiveFile }))
			add(p.name, task)
		}
	}
}

tasks.assemble {
	dependsOn(tasks.shadowJar)
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
	base.archivesName.set("archives_base_name"())
	version = "mod_version"() + (buildNumber?.let { "-build.$it" } ?: "")

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
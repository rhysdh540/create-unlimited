@file:Suppress("UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask
import xyz.wagyourtail.unimined.expect.task.ExpectPlatformFiles
import xyz.wagyourtail.unimined.internal.minecraft.MinecraftProvider
import xyz.wagyourtail.unimined.internal.minecraft.task.RemapJarTaskImpl
import xyz.wagyourtail.unimined.util.sourceSets
import java.util.*

plugins {
	id("java")
	id("xyz.wagyourtail.unimined")
	id("xyz.wagyourtail.unimined.expect-platform")
	id("com.github.johnrengelman.shadow")
}

setup()

allprojects {
	apply(plugin = "java")
	apply(plugin = "xyz.wagyourtail.unimined")
	apply(plugin = "xyz.wagyourtail.unimined.expect-platform")
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
		maven("https://maven.architectury.dev")
		exclusiveContent {
			forRepository { maven("https://cursemaven.com") }
			filter {
				includeGroup("curse.maven")
			}
		}
		maven("https://maven.wagyourtail.xyz/releases")
	}

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.release = 17
		options.compilerArgs.addAll(listOf("-Xplugin:Manifold no-bootstrap", "-implicit:none"))

		javaCompiler = javaToolchains.compilerFor {
			languageVersion.set(JavaLanguageVersion.of(17))
		}
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

		defaultRemapJar = false
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
		compileOnly("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")

		compileOnly(expectPlatform.annotationsDep)
	}
}

subprojects {
	val platform = project.name.lowercase()

	tasks.processResources {
		from(rootProject.sourceSets["main"].resources)

		val props = mapOf(
			"mod_version" to "modVersion"(),
			"minecraft_version" to "minecraft_version"(),
			"fabric_version" to "fabric_version"(),
			"create_version" to "minimum_create_version"(),
		)

		inputs.properties(props)

		filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml")) {
			expand(props)
		}
	}

	tasks.jar {
		archiveClassifier = "$platform-dev-unmapped"
		destinationDirectory.set(layout.buildDirectory.dir("devlibs"))
	}

	val common by configurations.registering {
		isTransitive = false
		configurations.compileClasspath.get().extendsFrom(this)
		configurations.runtimeClasspath.get().extendsFrom(this)
	}

	val expectPlatform by rootProject.tasks.register<ExpectPlatformFiles>("expectPlatform${platform.replaceFirstChar(Char::uppercase)}") {
		group = "unimined"
		platformName = platform
		inputCollection = rootProject.sourceSets["main"].output
	}

	dependencies {
		compileOnly(rootProject) // for ide
		common(expectPlatform.outputCollection)
	}

	tasks.shadowJar {
		dependsOn(expectPlatform)
		archiveBaseName.set("archives_base_name"())
		archiveVersion.set("modVersion"())
		archiveClassifier.set("$platform-unmapped")
		destinationDirectory.set(layout.buildDirectory.dir("devlibs"))

		configurations = listOf(common.get())

		relocate("dev.rdh.createunlimited.${project.name}", "dev.rdh.createunlimited.${project.name}.platform")
		relocate("dev.rdh.createunlimited", "dev.rdh.createunlimited.${project.name}")
	}

	val mcProvider = unimined.minecrafts[sourceSets["main"]]

	val remapShadowJar = tasks.register("remapShadowJar", RemapJarTaskImpl::class.java, mcProvider)
	remapShadowJar.configure {
		dependsOn("shadowJar")
		inputFile.set(tasks.shadowJar.get().archiveFile)
		archiveClassifier = platform
	}
}

tasks.jar { enabled = false }

unimined.minecraft {
	fabric { loader("fabric_version"()) }

	runs.off = true

	mods {
		modImplementation {
			catchAWNamespaceAssertion()
		}
	}

	runs {
		config("client") {
			launchClasspath
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

val mergeJars = tasks.register<ShadowJar>("mergeJars") {
	group = "build"
	description = "Merges the platform shadow jars into a single jar"
	archiveBaseName.set("archives_base_name"())
	archiveVersion.set("modVersion"())
	subprojects.map { it.tasks["remapShadowJar"] }.forEach {
		dependsOn(it)
		from(it)
	}
}

tasks.assemble {
	dependsOn(mergeJars)
}

fun setup() {
	println("Create Unlimited v${"mod_version"()}")

	val buildNumber: String? = System.getenv("GITHUB_RUN_NUMBER")
	if(buildNumber != null) {
		println("Build #$buildNumber")
	}
	println()
	println("Current branch: ${git.currentBranch()}")
	println("Current commit: ${git.hash()}")
	if(git.isDirty()) {
		var changes = git.getUncommitedChanges().split("\n").toMutableList()
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

	tasks.assemble {
		subprojects.forEach {
			this.dependsOn(it.tasks.named("assemble"))
		}
	}

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

operator fun String.invoke(): String = rootProject.ext[this] as String
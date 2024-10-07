@file:Suppress("UnstableApiUsage")

import proguard.ConfigurationParser
import proguard.ProGuard
import xyz.wagyourtail.commons.gradle.shadow.ShadowJar
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask
import xyz.wagyourtail.unimined.expect.task.ExpectPlatformJar
import xyz.wagyourtail.unimined.internal.minecraft.task.RemapJarTaskImpl
import xyz.wagyourtail.commons.gradle.sourceSets
import xyz.wagyourtail.commons.gradle.javaToolchains

plugins {
	id("java")
	id("idea")
	id("xyz.wagyourtail.unimined")
	id("xyz.wagyourtail.commons-gradle")
	id("org.jetbrains.gradle.plugin.idea-ext")
	id("xyz.wagyourtail.unimined.expect-platform")
}

setup()

allprojects {
	apply {
		plugin("java")
		plugin("idea")
		plugin("xyz.wagyourtail.unimined")
		plugin("xyz.wagyourtail.commons-gradle")
		plugin("org.jetbrains.gradle.plugin.idea-ext")
		plugin("xyz.wagyourtail.unimined.expect-platform")
	}

	base.archivesName.set("archives_base_name"())
	version = "modVersion"()
	group = "maven_group"()

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of("java_version"()))
		}

		withSourcesJar()
	}

	idea {
		module.isDownloadSources = true
	}

	repositories {
		unimined.parchmentMaven()
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
		unimined.wagYourMaven("releases")

		maven("https://maven.tterrag.com")
	}

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.release = "java_version"().toInt()
		options.compilerArgs.addAll(listOf("-Xplugin:Manifold no-bootstrap", "-implicit:none"))

		javaCompiler = javaToolchains.compilerFor {
			languageVersion.set(JavaLanguageVersion.of("java_version"()))
		}
	}

	tasks.withType<AbstractArchiveTask> {
		isPreserveFileTimestamps = false
		isReproducibleFileOrder = true
		includeEmptyDirs = false
	}

	unimined.minecraft(sourceSet = sourceSets["main"], lateApply = true) {
		version = "minecraft_version"()

		mappings {
			mojmap()
			parchment(version = "parchment_version"())

			devFallbackNamespace("official")
		}

		runs {
			config("client") {
				jvmArgs("-Xms4G", "-Xmx4G")
				expectPlatform.insertAgent(spec = this, platformName = project.name)
			}
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

	dependencies {
		implementation(rootProject.sourceSets["main"].output)
	}

	tasks.processResources {
		from(rootProject.sourceSets["main"].resources)

		val props = mapOf(
			"mod_version" to "modVersion"(),
			"minecraft_versions" to multiversion.minecraftVersions.joinToString(
				separator = when(platform) {
					"forge" -> "],["
					"fabric" -> "\",\""
					else -> error("Unknown platform $platform")
				}
			),
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
		from(rootProject.sourceSets["main"].output) {
			include("**/*.class")
		}
		putInDevlibs()
	}

	val expectPlatformJar by tasks.register<ExpectPlatformJar>("platformJar") {
		group = "unimined"
		platformName = platform
		archiveClassifier = "expect-$platform"
		putInDevlibs()
		inputFiles = files(tasks.jar.get().archiveFile)
	}

	val shadowJar by tasks.register<ShadowJar>("shadowJar") {
		dependsOn(expectPlatformJar)
		archiveBaseName.set("archives_base_name"())
		archiveVersion.set("modVersion"())
		archiveClassifier.set("$platform-shadowJar")
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
		putInDevlibs()

		shadowContents.add(files(expectPlatformJar.archiveFile))

		relocate("dev.rdh.createunlimited.$platform", "dev.rdh.createunlimited")
	}

	val remapPlatformJar = tasks.register<RemapJarTaskImpl>("remapPlatformJar", unimined.minecrafts[sourceSets["main"]])
	remapPlatformJar.configure {
		dependsOn(shadowJar)
		inputFile.set(shadowJar.archiveFile)
		archiveClassifier = platform
	}

	tasks.register<ShadowJar>("preShadow") {
		archiveClassifier.set("premerge-$platform")
		putInDevlibs()

		val oldMixinConfig = "createunlimited.mixins.json"
		val newMixinConfig = "createunlimited-$platform.mixins.json"

		from(zipTree(remapPlatformJar.get().archiveFile)) {
			includeEmptyDirs = false

			eachFile {
				if(path == "fabric.mod.json") {
					filter { it.replace(oldMixinConfig, newMixinConfig) }
				}

				if(path == oldMixinConfig) {
					filter { it.replace("dev.rdh.createunlimited.asm", "dev.rdh.createunlimited.$platform.asm") }
					path = newMixinConfig
				}
			}
		}

		relocate("com.llamalad7.mixinextras", "dev.rdh.createunlimited.mixinextras")
		relocate("dev.rdh.createunlimited", "dev.rdh.createunlimited.${project.name}")
	}
}

tasks.jar { enabled = false }

val modCompileOnly: Configuration by configurations.creating {
	configurations["compileClasspath"].extendsFrom(this)
}

unimined.minecraft {
	runs.off = true

	mappings {
		intermediary()
	}

	mods {
		modImplementation {
			catchAWNamespaceAssertion()
			namespace("intermediary")
		}

		remap(modCompileOnly) {
			catchAWNamespaceAssertion()
			namespace("intermediary")
		}
	}
}

repositories {
	maven("https://jitpack.io")
	maven("https://mvn.devos.one/snapshots")
	maven("https://maven.theillusivec4.top")
	maven("https://maven.cafeteria.dev/releases")
	maven("https://maven.jamieswhiteshirt.com/libs-release")
	maven("https://repo.spongepowered.org/repository/maven-public/")
}

val shadow: Configuration by configurations.creating

dependencies {
	modCompileOnly("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric_version"()}+mc${"minecraft_version"()}") {
		exclude(group = "com.github.llamalad7.mixinextras", module = "mixinextras-fabric")
	}

	implementation("org.ow2.asm:asm:${"asm_version"()}")
	implementation("org.ow2.asm:asm-tree:${"asm_version"()}")
	implementation("org.ow2.asm:asm-commons:${"asm_version"()}")
	implementation("org.spongepowered:mixin:${"mixin_version"()}")

	shadow("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")
}

val mergeJars by tasks.register<ShadowJar>("mergeJars") {
	group = "build"
	description = "Merges the platform shadow jars into a single jar"
	archiveBaseName = "archives_base_name"()
	archiveVersion = "modVersion"()
	archiveClassifier = "merged"
	putInDevlibs()

	includeEmptyDirs = false
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE

	from(subprojects.map { zipTree(it.tasks.get<ShadowJar>("preShadow").archiveFile) }) {
		includeEmptyDirs = false
	}

	from(shadow.map { zipTree(it) }) {
		includeEmptyDirs = false
		include("**/*.class")
	}

	relocate("com.llamalad7.mixinextras", "dev.rdh.createunlimited.mixinextras")

	manifest {
		attributes["MixinConfigs"] = "createunlimited-forge.mixins.json"
		attributes["Fabric-Loom-Mixin-Remap-Type"] = "static"
	}
}

val compressJar = tasks.register<ProcessJar>("compressJar") {
	input.set(mergeJars.archiveFile)
	description = "Compresses the merged jar"

	archiveBaseName = "archives_base_name"()
	archiveVersion = "modVersion"()
	archiveClassifier = ""

	addFileProcessor(extensions = setOf("json", "mcmeta"), processor = Compressors.json)
	addFileProcessor(extensions = setOf("jar"), processor = Compressors.storeJars)

	addDirProcessor { dir -> // proguard
		val temp = temporaryDir.resolve("proguard")
		temp.mkdirs()
		dir.copyRecursively(temp, overwrite = true)
		dir.deleteRecursively()
		val args = mutableListOf(
			"@${file("proguard.pro").absolutePath}",
			"-injars", temp.absolutePath,
			"-outjars", dir.absolutePath,
		)

		val libraries = mutableSetOf<String>()
		libraries.add("${JAVA_HOME}/jmods/java.base.jmod")

		for (minecraftConfig in subprojects.flatMap { it.unimined.minecrafts.values }) {
			val prodNamespace = minecraftConfig.mcPatcher.prodNamespace

			libraries.add(minecraftConfig.getMinecraft(prodNamespace, prodNamespace).toFile().absolutePath)

			val minecrafts = listOf(
				minecraftConfig.sourceSet.compileClasspath.files,
				minecraftConfig.sourceSet.runtimeClasspath.files
			).flatten()
				.filter { !minecraftConfig.isMinecraftJar(it.toPath()) }
				.toHashSet()

			libraries += minecraftConfig.mods.getClasspathAs(prodNamespace, prodNamespace, minecrafts)
				.filter { it.extension == "jar" && !it.name.startsWith("createunlimited") }
				.map { it.absolutePath }
		}

		args.addAll(listOf("-libraryjars", libraries.joinToString(separator = File.pathSeparator) { "\"$it\"" }))

		try {
			ProGuard(proguard.Configuration().also {
				ConfigurationParser(args.toTypedArray(), null)
					.parse(it)
			}).execute()
		} catch (ex: Exception) {
			throw IllegalStateException("ProGuard failed for $temp", ex)
		} finally {
			temp.deleteRecursively()
		}
	}
}

tasks.assemble {
	dependsOn(mergeJars, compressJar)
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

	ext["modVersion"] = "mod_version"() + (buildNumber?.let { "-build.$it" } ?: "")

	tasks.assemble {
		subprojects.forEach {
			this.dependsOn(it.tasks.named("assemble"))
		}
	}

	multiversion.findAndLoadProperties()
}

tasks.register("nukeGradleCaches") {
	dependsOn("clean")
	group = "build"
	description = "Deletes all .gradle directories in the project. WARNING: causes IDEs to freeze for a while."
	outputs.upToDateWhen { false }
	doLast {
		allprojects.forEach {
			it.file(".gradle").deleteRecursively()
		}
	}
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("No property \"$this\"")
import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import net.fabricmc.loom.util.TinyRemapperHelper
import net.fabricmc.loom.util.TinyRemapperLoggerAdapter
import net.fabricmc.tinyremapper.TinyRemapper
import net.fabricmc.tinyremapper.extension.mixin.MixinExtension
import net.neoforged.moddevgradle.internal.RunGameTask
import net.neoforged.moddevgradle.legacyforge.tasks.RemapJar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType
import xyz.wagyourtail.unimined.expect.task.ExpectPlatformJar

plugins {
	id("net.neoforged.moddev.legacyforge")
}

legacyForge {
	version = "${"minecraft_version"()}-${"forge_version"()}"

	parchment {
		minecraftVersion = "minecraft_version"()
		mappingsVersion = "parchment_version"()
	}

	runs {
		create("client") {
			client()
		}

		create("server") {
			server()
		}

		all {
			systemProperty("mixin.debug.export", "true")
			systemProperty("mixin.env.remapRefMap", "true")
			systemProperty("mixin.env.refMapRemappingFile", project.layout.buildDirectory.map { it.file("moddev/artifacts/intermediateToNamed.srg") }.get().asFile.absolutePath)
		}
	}
}

mixin {
	config("createunlimited.mixins.json")
}

dependencies {
	implementation(rootProject.sourceSets["main"].output)
	modImplementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge_version"()}:slim")
	modImplementation("net.createmod.ponder:Ponder-Forge-${"minecraft_version"()}:${"ponder_version"()}")
	modImplementation("com.tterrag.registrate:Registrate:${"registrate_version"()}")
	modCompileOnly("dev.engine-room.flywheel:flywheel-forge-api-${"minecraft_version"()}:${"flywheel_version"()}")
	modRuntimeOnly("dev.engine-room.flywheel:flywheel-forge-${"minecraft_version"()}:${"flywheel_version"()}")
	modImplementation("io.github.llamalad7:mixinextras-forge:${"mixin_extras_version"()}")
}

tasks.jar {
	putInDevlibs()
	from(rootProject.sourceSets["main"].output)
}

val expectPlatformJar by tasks.registering<ExpectPlatformJar> {
	putInDevlibs()
	group = "build"
	inputFiles = files(tasks.jar.get().archiveFile)
	platformName = "forge"
	archiveClassifier.set("expect")
}

tasks.shadowJar {
	clearSourcePaths()
	putInDevlibs()
	archiveClassifier = "shadow"

	configurations.empty()
	from(zipTree(expectPlatformJar.get().archiveFile))

	relocate(SimpleRelocator(
		pattern = "dev.rdh.createunlimited",
		shadedPattern = "dev.rdh.createunlimited.forge",
		includes = listOf("dev.rdh.createunlimited.**"),
		excludes = listOf("dev.rdh.createunlimited.forge.**")
	))

	eachFile {
		val oldMixinPackage = "dev.rdh.createunlimited"
		val newMixinPackage = "dev.rdh.createunlimited.forge"
		if (name.endsWith(".mixins.json")) {
			name = "${name.removeSuffix(".mixins.json")}-forge.mixins.json"
			filter {
				it.replace(oldMixinPackage, newMixinPackage)
			}
		}
	}
}

tasks.named<RemapJar>("reobfJar") {
	enabled = false
}

tasks.register<BetterRemapJar>("remapJar") {
	val oldRemapJar = tasks.getByName<RemapJar>("reobfJar")
	inputFile = tasks.shadowJar.map { it.archiveFile.get() }
	mappings = oldRemapJar.remapOperation.mappings.files.single()
	libraries = oldRemapJar.libraries

	manifest.attributes(
		"MixinConfigs" to "createunlimited-forge.mixins.json",
	)
}

tasks.assemble {
	dependsOn(tasks["remapJar"])
}

// we d a little trolling
abstract class BetterRemapJar : Jar() {
	@get:InputFile
	abstract val inputFile: RegularFileProperty

	@get:InputFile
	abstract val mappings: RegularFileProperty

	@get:InputFiles
	abstract val libraries: ConfigurableFileCollection

	@get:Inject
	abstract val archiveOps: ArchiveOperations

	init {
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	}

	@TaskAction
	fun execute() {
		val mapper = TinyRemapper.newRemapper(TinyRemapperLoggerAdapter.INSTANCE)
			.withMappings(TinyRemapperHelper.create(
				mappings.get().asFile.toPath(),
				"source", "target",
				true
			))
			.extension(MixinExtension())
			.ignoreFieldDesc(true)
			.build()

		mapper.readClassPath(*libraries.map { it.toPath() }.toTypedArray())
		mapper.readInputs(inputFile.get().asFile.toPath())

		val output = temporaryDir.resolve("output")
		output.mkdirs()

		val names = mutableSetOf<String>()

		mapper.apply { name, bytes ->
			names.add("$name.class")
			val outputFile = output.resolve("$name.class")
			outputFile.parentFile.mkdirs()
			outputFile.writeBytes(bytes)
		}

		mapper.finish()

		from(output)
		from(archiveOps.zipTree(inputFile.get().asFile)) {
			exclude { it.name in names }
		}

		super.copy()
	}
}

tasks.processResources {
	val props = mapOf(
		"mod_version" to rootProject.version,
		"minecraft_versions" to multiversion.minecraftVersions.joinToString("],["),
		"create_version" to "minimum_create_version"(),
	)

	inputs.properties(props)

	filesMatching("META-INF/mods.toml") {
		expand(props)
	}
}

afterEvaluate {
	tasks.withType<RunGameTask>().configureEach {
		expectPlatform.insertAgent(this, "forge")
	}
}

operator fun String.invoke() = prop(this)
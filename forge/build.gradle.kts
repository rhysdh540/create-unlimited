import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import net.fabricmc.loom.util.TinyRemapperHelper
import net.fabricmc.loom.util.TinyRemapperLoggerAdapter
import net.fabricmc.tinyremapper.TinyRemapper
import net.fabricmc.tinyremapper.extension.mixin.MixinExtension
import net.neoforged.moddevgradle.internal.RunGameTask
import net.neoforged.moddevgradle.legacyforge.tasks.RemapJar
import net.neoforged.moddevgradle.legacyforge.tasks.RemapOperation
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

	// tell forge that the se should be in the same module
	mods.maybeCreate("main").apply {
		sourceSet(sourceSets["main"])
		sourceSet(rootProject.sourceSets["main"])
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
	config(tasks.named<RemapJar>("reobfJar"))
	input.set(tasks.shadowJar.map { it.archiveFile.get() })

	manifest.attributes(
		"MixinConfigs" to "createunlimited-forge.mixins.json",
	)
}

tasks.assemble {
	dependsOn(tasks["remapJar"])
}

// we do a little trolling
@CacheableTask
abstract class BetterRemapJar : Jar() {
	@get:InputFile
	@get:PathSensitive(PathSensitivity.NONE)
	abstract val input: RegularFileProperty

	@get:Nested
	abstract val remapOperation: RemapOperation

	@get:Optional
	@get:InputFiles
	@get:PathSensitive(PathSensitivity.NONE)
	abstract val libraries: ConfigurableFileCollection

	@get:Inject
	abstract val archiveOps: ArchiveOperations

	init {
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	}

	fun config(task: TaskProvider<RemapJar>) {
		this.libraries.from(task.map { it.libraries })
		this.remapOperation.mappings.from(task.map { it.remapOperation.mappings })
		this.remapOperation.toolType.set(task.flatMap { it.remapOperation.toolType })
	}

	@TaskAction
	fun execute() {
		val mappings = remapOperation.mappings.files.single()
		val input = input.get().asFile
		val mapper = TinyRemapper.newRemapper(TinyRemapperLoggerAdapter.INSTANCE)
			.withMappings(TinyRemapperHelper.create(
				mappings.toPath(),
				// these *are* important
				"source", "target",
				true
			))
			.extension(MixinExtension())
			.ignoreFieldDesc(true)
			.build()

		mapper.readClassPath(*libraries.map { it.toPath() }.toTypedArray())
		mapper.readInputs(input.toPath())

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
		from(archiveOps.zipTree(input)) {
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
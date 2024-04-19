import net.fabricmc.loom.api.LoomGradleExtensionAPI

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.kotlin.dsl.*

@Suppress("UnstableApiUsage")
class SubprojectsPlugin : Plugin<Project> {
	private lateinit var project: Project

	override fun apply(project: Project) {
		this.project = project
		project.run {
			apply(plugin = "dev.architectury.loom")
			apply(plugin = "architectury-plugin")

			val loom = extensions.getByType<LoomGradleExtensionAPI>()

			loom.silentMojangMappingsLicense()
			extensions.getByType<JavaPluginExtension>().withSourcesJar()

			val shade: Configuration by configurations.creating
			configurations["implementation"].extendsFrom(shade)

			repositories {
				mavenCentral()
				mavenLocal()
				maven("https://maven.parchmentmc.org", exclusive = true) {
					listOf("org.parchmentmc.data")
				}
				maven("https://maven.quiltmc.org/repository/release", exclusive = true) {
					listOf("org.quiltmc")
				}
				maven("https://maven.ithundxr.dev/releases")
				maven("https://mvn.devos.one/snapshots")
				maven("https://maven.cafeteria.dev/releases")
				maven("https://maven.jamieswhiteshirt.com/libs-release")
				maven("https://maven.theillusivec4.top")
				maven("https://maven.terraformersmc.com/releases")
				maven("https://jitpack.io")
				maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
				maven("https://maven.tterrag.com") {
					listOf("com.simibubi.create", "com.jozufozu.flywheel", "com.tterrag.registrate")
				}
				maven("https://api.modrinth.com/maven", exclusive = true) {
					listOf("maven.modrinth")
				}
				maven("https://cursemaven.com", exclusive = true) {
					listOf("curse.maven")
				}
			}

			val mcVersion = project.property("minecraft_version") as String

			dependencies {
				"minecraft"("com.mojang:minecraft:$mcVersion")
				"mappings"(loom.layered {
					mappings("org.quiltmc:quilt-mappings:$mcVersion+build.${project.property("quilt")}:intermediary-v2")
					officialMojangMappings()
					parchment("org.parchmentmc.data:parchment-$mcVersion:${project.property("parchment")}@zip")
				})

				manifold("props")
				manifold("ext")
				"localRuntime"("systems.manifold:manifold-ext-rt:${project.property("manifold_version")}")

				"io.github.llamalad7:mixinextras-common:${project.property("mixin_extras")}".also {
					"annotationProcessor"(it)
					"implementation"(it)
					if(project.getPath() != ":common") {
						shade(it)
					}
				}
			}

			tasks.named("processResources", AbstractCopyTask::class.java) {
				val properties = mapOf(
					"version" to project.property("modVersion"),
					"minecraft" to mcVersion,
					"fabric" to project.property("fabric"),
					"create" to project.property("minimum_create_version"),
				)

				inputs.properties(properties)
				exclude("**/*.aw")
				filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml")) {
					expand(properties)
				}
			}
		}
	}

	private fun DependencyHandler.manifold(module: String) {
		val location = "systems.manifold:manifold-" + module + ":" + project.property("manifold_version")
		add("annotationProcessor", location)
		add("compileOnly", location)
		if (project.getPath() != ":common") {
			add("localRuntime", location)
		}
		if (project.getPath() == ":forge") {
			add("forgeRuntimeLibrary", location)
		}
	}

	private fun RepositoryHandler.maven(url: String, exclusive: Boolean = false, includes: () -> List<String>) {
		if(exclusive) {
			exclusiveContent {
				forRepository { maven(url) }
				filter {
					includes().forEach { includeGroup(it) }
				}
			}
		} else {
			maven(url).apply {
				includes().forEach {
					content {
						includeGroup(it)
					}
				}
			}
		}
	}
}
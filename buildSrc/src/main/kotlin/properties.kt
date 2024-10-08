import org.gradle.api.Project
import java.util.*

val Project.multiversion: Multiversion
	get() = Multiversion(this)

class Multiversion(private val project: Project) {
	fun findAndLoadProperties() {
		project.run {
			val mcVers = this@Multiversion.minecraftVersions

			println("Setting up properties...")
			println("Available Minecraft Versions: ${mcVers.joinToString(", ")}")

			fun err(): Nothing = error("Invalid Minecraft version")

			val mcVersion = project.findProperty("mcVer") as? String ?: run {
				println("No mcVer set!")
				println("Use -PmcVer='mc_version' or edit gradle.properties to set the minecraft version.")
				err()
			}

			if (mcVersion !in mcVers) {
				println("Invalid Minecraft version: $mcVersion")
				println("Available Minecraft Versions: ${mcVers.joinToString(", ")}")
				err()
			}

			println("Using Minecraft $mcVersion")
			val properties = Properties().apply {
				load(rootProject.file("versionProperties/$mcVersion.properties").reader())
			}

			properties["minecraft_version"].let {
				if(it == null) {
					println("No minecraft_version property found in $mcVersion.properties")
					err()
				}

				if(it != mcVersion) {
					println("minecraft_version property in $mcVersion.properties does not match the selected version ($mcVersion)")
					err()
				}
			}

			properties.forEach { key, value ->
				project.extensions.extraProperties[key.toString()] = value
			}
		}
	}

	val minecraftVersions by lazy {
		project.rootProject.fileTree("versionProperties").files.stream()
			.map { it.getName().removeSuffix(".properties") }
			.sorted()
			.toList()
			.toSortedSet()
	}
}
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

class PropertiesPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.run {
			var mcVersion = ""
			val mcVers = rootProject.fileTree("versionProperties").files.stream()
				.map { it.getName().removeSuffix(".properties") }
				.sorted()
				.toList()

			println("Setting up properties...")
			println("Avalible Minecraft Versions: ${mcVers.joinToString(", ")}")

			if(rootProject.hasProperty("mcVer")) {
				mcVersion = rootProject.properties["mcVer"] as String
			}

			if(!mcVers.contains(mcVersion)) {
				println("No mcVer set or the set mcVer is invalid!")
				println("Use -PmcVer='mc_version' or edit gradle.properties to set the minecraft version.")
				throw InvalidPropertiesFormatException("Invalid minecraft version")
			}

			println("Using Minecraft $mcVersion")

			val properties = Properties().apply {
				load(rootProject.file("versionProperties/$mcVersion.properties").reader())
			}

			properties.forEach { (key, value) ->
				project.extensions.extraProperties[key.toString()] = value
			}
		}
	}
}
import org.gradle.api.Project
import java.util.*

fun Project.findAndLoadProperties() {
	val mcVers = rootProject.fileTree("versionProperties").files.stream()
		.map { it.getName().removeSuffix(".properties") }
		.sorted()
		.toList()

	println("Setting up properties...")
	println("Avalible Minecraft Versions: ${mcVers.joinToString(", ")}")

	val mcVersion: String = findProperty("mcVer") as? String ?: run {
		println("No mcVer set or the set mcVer is invalid!")
		println("Use -PmcVer='mc_version' or edit gradle.properties to set the minecraft version.")
		error("Invalid Minecraft version")
	}

	println("Using Minecraft $mcVersion")
	val properties = Properties().apply {
		load(rootProject.file("versionProperties/$mcVersion.properties").reader())
	}

	properties.forEach { (key, value) ->
		project.extensions.extraProperties[key.toString()] = value
	}
}
import org.gradle.api.Project
import java.util.*

fun Project.findAndLoadProperties() {
	val mcVers = minecraftVersions()

	println("Setting up properties...")
	println("Available Minecraft Versions: ${mcVers.joinToString(", ")}")

	val mcVersion = findProperty("mcVer") as? String ?: run {
		println("No mcVer set!")
		println("Use -PmcVer='mc_version' or edit gradle.properties to set the minecraft version.")
		error("Invalid Minecraft version")
	}

	if (mcVersion !in mcVers) {
		println("Invalid Minecraft version!")
		println("Available Minecraft Versions: ${mcVers.joinToString(", ")}")
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

fun Project.minecraftVersions(): List<String> {
	return rootProject.fileTree("versionProperties").files.stream()
		.map { it.getName().removeSuffix(".properties") }
		.sorted()
		.toList()
}
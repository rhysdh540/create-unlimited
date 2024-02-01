plugins {
	id("java-gradle-plugin")
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://maven.fabricmc.net/")
	maven("https://maven.architectury.dev/")
	maven("https://maven.minecraftforge.net/")
	maven("https://maven.firstdarkdev.xyz/releases")
	gradlePluginPortal()
}

fun DependencyHandler.plugin(id: String, version: String) {
	this.implementation(group = id, name = "$id.gradle.plugin", version = version)
}

dependencies {
	plugin("architectury-plugin", "3.4.151")
	plugin("dev.architectury.loom", "1.4.380")
	plugin("com.github.johnrengelman.shadow", "8.1.1")
	plugin("io.github.pacifistmc.forgix", "1.2.6")

	implementation("org.ow2.asm:asm:9.6")
	implementation("org.ow2.asm:asm-analysis:9.6")
}

gradlePlugin {
	plugins {
		create("properties") {
			id = "properties"
			implementationClass = "PropertiesPlugin"
		}

		create("subprojects") {
			id = "subprojects"
			implementationClass = "SubprojectsPlugin"
		}

		create("platform") {
			id = "platform"
			implementationClass = "PlatformPlugin"
		}

		create("postprocessor") {
			id = "postprocessor"
			implementationClass = "JarPostProcessorPlugin"
		}
	}
}
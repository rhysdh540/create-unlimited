plugins {
	id("java-gradle-plugin")
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://maven.fabricmc.net")
	maven("https://maven.architectury.dev")
	maven("https://maven.minecraftforge.net")
	maven("https://mcentral.firstdark.dev/releases")
	gradlePluginPortal()
}

fun DependencyHandler.plugin(id: String, version: String) {
	this.implementation(group = id, name = "$id.gradle.plugin", version = version)
}

dependencies {
	plugin(id = "com.github.johnrengelman.shadow", version = "8.1.1")
	plugin(id = "xyz.wagyourtail.unimined", version = "1.2.0-SNAPSHOT")

	implementation("org.ow2.asm:asm:9.6")
	implementation("org.ow2.asm:asm-analysis:9.6")
}

gradlePlugin {
	plugins {
		create("properties") {
			id = "properties"
			implementationClass = "PropertiesPlugin"
		}

		create("postprocessor") {
			id = "postprocessor"
			implementationClass = "JarPostProcessorPlugin"
		}
	}
}
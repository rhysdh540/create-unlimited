import xyz.wagyourtail.commons.gradle.shadow.ShadowJar
import xyz.wagyourtail.unimined.expect.task.ExpectPlatformJar

plugins {
	id("fabric-loom")
}

val shadowJar by tasks.registering<ShadowJar> {
	dependsOn(tasks.named("expectPlatformJar"))
	group = "build"
	archiveClassifier.set("shadow")
	from(zipTree(tasks.getByName<ExpectPlatformJar>("expectPlatformJar").archiveFile.get()))

	relocate("dev.rdh.createunlimited.fabric", "dev.rdh.createunlimited")
}

tasks.remapJar {

}

repositories {
	maven("https://maven.terraformersmc.com")
	maven("https://mvn.devos.one/releases")
	maven("https://mvn.devos.one/snapshots")
	maven("https://maven.cafeteria.dev/releases")
	maven("https://maven.jamieswhiteshirt.com/libs-release")
	maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
}

dependencies {
	minecraft("com.mojang:minecraft:${"minecraft_version"()}")
	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-${"minecraft_version"()}:${"parchment_version"()}@zip")
	})
	modImplementation("net.fabricmc:fabric-loader:${"fabric_version"()}")

	modImplementation("net.fabricmc.fabric-api:fabric-api:${"fabric_api_version"()}+${"minecraft_version"()}")
	runtimeOnly("ca.weblite:java-objc-bridge:1.1")

	modImplementation("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric_version"().split("$$").joinToString("+mc${"minecraft_version"()}-build.")}")

	modImplementation("com.terraformersmc:modmenu:${"modmenu_version"()}")

	// have deprecated modules present at runtime only
	if("minecraft_version"() != "1.18.2") {
		modLocalRuntime("net.fabricmc.fabric-api:fabric-api-deprecated:${"fabric_api_version"()}+${"minecraft_version"()}")
	}

	// Dev Env Optimizations
	if (rootProject.hasProperty("lazydfu_version")) {
		modLocalRuntime("maven.modrinth:lazydfu:${"lazydfu_version"()}")
	}
}

operator fun String.invoke() = rootProject.ext[this] as? String ?: error("No property \"$this\"")
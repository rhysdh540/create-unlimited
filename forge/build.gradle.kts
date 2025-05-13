import org.gradle.kotlin.dsl.getByName
import xyz.wagyourtail.commons.gradle.shadow.ShadowJar
import xyz.wagyourtail.unimined.expect.task.ExpectPlatformJar

plugins {
	id("net.neoforged.moddev.legacyforge")
}

legacyForge {
	version = "${"minecraft_version"()}-${"forge_version"()}"

	parchment {
		minecraftVersion.set("minecraft_version"())
		mappingsVersion.set("parchment_version"())
	}
}

val shadowJar by tasks.registering<ShadowJar> {
	dependsOn(tasks.named("expectPlatformJar"))
	group = "build"
	archiveClassifier.set("shadow")
	from(zipTree(tasks.getByName<ExpectPlatformJar>("expectPlatformJar").archiveFile.get()))

	relocate("dev.rdh.createunlimited.forge", "dev.rdh.createunlimited")
}

dependencies {
	implementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge_version"()}:slim") { isTransitive = false }
	implementation("net.createmod.ponder:Ponder-Forge-${"minecraft_version"()}:${"ponder_version"()}")
	implementation("com.tterrag.registrate:Registrate:${"registrate_version"()}")
	implementation("dev.engine-room.flywheel:flywheel-forge-api-${"minecraft_version"()}:${"flywheel_version"()}")
	implementation("io.github.llamalad7:mixinextras-forge:${"mixin_extras_version"()}")
	runtimeOnly("dev.engine-room.flywheel:flywheel-forge-${"minecraft_version"()}:${"flywheel_version"()}")
}

operator fun String.invoke() = rootProject.ext[this] as? String ?: error("No property \"$this\"")
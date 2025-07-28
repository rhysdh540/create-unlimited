import net.fabricmc.loom.util.TinyRemapperHelper
import net.neoforged.moddevgradle.legacyforge.tasks.RemapJar

plugins {
	id("net.neoforged.moddev.legacyforge")
}

legacyForge {
	version = "minecraft_version"() + "-" + "forge_version"()
}

mixin {
	config("createunlimited.mixins.json")
}

dependencies {
	modRuntimeOnly("io.github.llamalad7:mixinextras-forge:${"mixinextras_version"()}")
	compileOnly("io.github.llamalad7:mixinextras-common:${"mixinextras_version"()}")
}

tasks.named<RemapJar>("reobfJar") {
	enabled = false
}

tasks.register<BetterRemapJar>("remapJar") {
	config(tasks.named<RemapJar>("reobfJar"))
	input.set(tasks.jar.map { it.archiveFile.get() })

	manifest.attributes(
		"MixinConfigs" to "createunlimited.mixins.json",
	)

}

apply(plugin = "cu-forge-common")

operator fun String.invoke() = prop(this)
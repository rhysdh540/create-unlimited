plugins {
	id("com.github.johnrengelman.shadow")
	id("platform")
}

architectury.forge()

configurations {
	"developmentForge" {
		extendsFrom(getByName("common"))
	}
}

dependencies {
	forge("net.minecraftforge:forge:${"minecraft_version"()}-${"forge"()}")

	modImplementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge"()}:slim") { isTransitive = false }
	modImplementation("com.tterrag.registrate:Registrate:${"registrate"()}")
	modImplementation("com.jozufozu.flywheel:flywheel-forge-${"flywheel_mc"()}:${"flywheel"()}")
	forgeRuntimeLibrary("io.github.llamalad7:mixinextras-common:${"mixin_extras"()}")
	include("io.github.llamalad7:mixinextras-forge:${"mixin_extras"()}")

	// Dev Env Optimizations
	if(rootProject.hasProperty("bmb")) {
		modRuntimeOnly("curse.maven:better-mods-button-541584:${"bmb"()}")
	}
	if(rootProject.hasProperty("catalogue")) {
		modRuntimeOnly("curse.maven:catalogue-459701:${"catalogue"()}")
	}
}

loom {
	forge {
		mixinConfig(
			"createunlimited.mixins.json",
		)
	}
}

tasks.shadowJar {
	exclude("fabric.mod.json")
}

operator fun String.invoke(): String = rootProject.ext[this] as? String
	?: error("Property $this is not defined")

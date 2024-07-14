import xyz.wagyourtail.unimined.api.unimined
import xyz.wagyourtail.unimined.internal.minecraft.patch.forge.ForgeLikeMinecraftTransformer
import xyz.wagyourtail.unimined.util.OSUtils

unimined.minecraft {
	minecraftForge {
		loader("forge_version"())
		mixinConfig("createunlimited.mixins.json")
	}

	runs.config("client") {
		jvmArgs(
			"-Dmixin.env.remapRefMap=true",
			"-Dmixin.env.refMapRemappingFile=${(mcPatcher as ForgeLikeMinecraftTransformer).srgToMCPAsSRG}"
		)

		if(OSUtils.oSId == OSUtils.OSX) {
			// for some reason this doesn't get inserted automatically on forge?
			jvmArgs("-XstartOnFirstThread")
		}
	}
}

repositories {
	maven("https://maven.tterrag.com")
}

dependencies {
	modImplementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge_version"()}:slim") { isTransitive = false }
	modImplementation("com.tterrag.registrate:Registrate:${"registrate_version"()}")
	modImplementation("com.jozufozu.flywheel:flywheel-forge-${"flywheel_mc_version"()}:${"flywheel_version"()}")
	implementation("io.github.llamalad7:mixinextras-forge:${"mixin_extras_version"()}") {
		"include"(this)
	}
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("Property $this is not defined")
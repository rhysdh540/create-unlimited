import xyz.wagyourtail.unimined.api.unimined
import xyz.wagyourtail.unimined.internal.minecraft.patch.forge.ForgeLikeMinecraftTransformer
import xyz.wagyourtail.unimined.util.OSUtils

unimined.minecraft {
	minecraftForge {
		loader("forge_version"())
		mixinConfig("createunlimited.mixins.json")
		accessTransformer(file("run/accesstransformer.cfg"))
	}

	mappings {
		stub.withMappings("searge", "mojmap") {
			c(when (minecraft.version) {
				"1.19.2" -> "exm"
				"1.20.1" -> "fho"
				else -> error("no ParticleEngine mapping for ${minecraft.version}")
			}, listOf("net/minecraft/client/particle/ParticleEngine")) {
				f("[nothing]", "Ljava/util/Map;", "f_107293_", "providers")
			}
		}
	}


	runs.all {
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

dependencies {
	modImplementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge_version"()}:slim") { isTransitive = false }
	modImplementation("com.tterrag.registrate:Registrate:${"registrate_version"()}")
	modImplementation("com.jozufozu.flywheel:flywheel-forge-${"flywheel_mc_version"()}:${"flywheel_version"()}")
}

operator fun String.invoke() = rootProject.ext[this] as? String ?: error("No property \"$this\"")
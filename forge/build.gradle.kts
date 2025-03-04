import xyz.wagyourtail.unimined.api.unimined
import xyz.wagyourtail.unimined.internal.minecraft.patch.forge.ForgeLikeMinecraftTransformer
import xyz.wagyourtail.unimined.util.OSUtils

val modRuntimeOnly: Configuration by configurations.creating {
	configurations["runtimeClasspath"].extendsFrom(this)
	isCanBeConsumed = false
	isCanBeResolved = true
}

unimined.minecraft {
	minecraftForge {
		loader("forge_version"())
		mixinConfig("createunlimited.mixins.json")
		accessTransformer(file("run/accesstransformer.cfg"))
	}

	mappings {
		stub.withMappings("searge", "mojmap") {
			c(when (minecraft.version) {
				"1.20.1" -> "fho"
				else -> error("no ParticleEngine mapping for ${minecraft.version}")
			}, listOf("net/minecraft/client/particle/ParticleEngine")) {
				f("[nothing]", "Ljava/util/Map;", "f_107293_", "providers")
			}
		}
	}

	mods {
		modImplementation {
			catchAWNamespaceAssertion()
		}
		remap(modRuntimeOnly)
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

	runs.config("client") {
		args("-mixin.config=create.mixins.json")
	}
}

dependencies {
	modImplementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge_version"()}:slim") { isTransitive = false }
	modImplementation("net.createmod.ponder:Ponder-Forge-${"minecraft_version"()}:${"ponder_version"()}")
	modImplementation("com.tterrag.registrate:Registrate:${"registrate_version"()}")
	modImplementation("dev.engine-room.flywheel:flywheel-forge-api-${"minecraft_version"()}:${"flywheel_version"()}")
	modImplementation("io.github.llamalad7:mixinextras-forge:${"mixin_extras_version"()}")
	modRuntimeOnly("dev.engine-room.flywheel:flywheel-forge-${"minecraft_version"()}:${"flywheel_version"()}")
}

operator fun String.invoke() = rootProject.ext[this] as? String ?: error("No property \"$this\"")
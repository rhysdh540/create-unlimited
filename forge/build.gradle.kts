import xyz.wagyourtail.unimined.api.unimined
import xyz.wagyourtail.unimined.internal.minecraft.patch.forge.ForgeLikeMinecraftTransformer

unimined.minecraft {
	minecraftForge {
		loader("forge_version"())
		mixinConfig("createunlimited.mixins.json")
	}

	runs.config("client") {
		jvmArgs.addAll(listOf(
			"-Dmixin.env.remapRefMap=true",
			"-Dmixin.env.refMapRemappingFile=${(mcPatcher as ForgeLikeMinecraftTransformer).srgToMCPAsSRG}"
		))
	}
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("Property $this is not defined")
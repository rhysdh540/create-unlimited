-ignorewarnings
-dontnote
-dontobfuscate
-optimizationpasses 10
-optimizations !class/merging/*,!method/marking/private,!method/marking/static,!*/specialization/*,!method/removal/parameter
-allowaccessmodification
#noinspection ShrinkerInvalidFlags
-optimizeaggressively
-keepattributes Runtime*Annotations,AnnotationDefault # keep annotations

-keep,allowoptimization @org.spongepowered.asm.mixin.Mixin class * {
	@org.spongepowered.asm.mixin.Overwrite *;
	@org.spongepowered.asm.mixin.Shadow *;
}

-keep,allowoptimization @*.*.fml.common.Mod class * {
	public <init>(...);
}

-keep,allowoptimization class * implements net.fabricmc.api.ModInitializer

-keep,allowoptimization class * implements com.terraformersmc.modmenu.api.ModMenuApi

-keep,allowoptimization class * implements org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
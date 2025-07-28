import org.gradle.api.internal.FeaturePreviews.Feature

enableFeaturePreview(Feature.STABLE_CONFIGURATION_CACHE.name)

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
	id("dev.kikugie.stonecutter") version "0.7.6"
}

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"

	create(rootProject) {
		fun add(mcVersion: String, vararg loaders: String) =
			loaders.forEach { vers("$mcVersion-$it", mcVersion) }

		add("1.20.1", "forge", "fabric")
		add("1.21.1", "neoforge")

		vcsVersion = "1.20.1-forge"
	}
}

rootProject.name = "Create Unlimited"

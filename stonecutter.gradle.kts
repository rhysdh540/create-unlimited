import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.stonecutter

plugins {
	id("base")
	id("dev.kikugie.stonecutter")
	id("xyz.wagyourtail.manifold")
}
stonecutter active "1.20.1-forge"

rootProject.group = "dev.rdh"
rootProject.base.archivesName.set("createunlimited")
rootProject.version = prop("mod_version")

manifold {
	version = prop("manifold_version")
	pluginArgs.add("--no-bootstrap")

	subprojectPreprocessor {
		for (p in project.subprojects) {
			val stonecutter = p.the<dev.kikugie.stonecutter.build.StonecutterBuildExtension>()
			subproject(p) {
				property("MC", stonecutter.current.version.removePrefix("1."))
				stonecutter.constants.forEach {
					if (it.value) { property(it.key) }
				}
			}
		}

		if (stonecutter.current != null) {
			ideActiveSubproject = stonecutter.current!!.project
		}
	}
}

tasks.register<MergedJar>("mergeJars") {
	group = "build"

	main(project("1.21.1-neoforge").tasks.named<Jar>("jar"))
	add("1.20.1-forge", project("1.20.1-forge").tasks.named<BetterRemapJar>("remapJar"))
	add("1.20.1-fabric", project("1.20.1-fabric").tasks.named<RemapJarTask>("remapJar"))

	archiveClassifier.set("merged")
}